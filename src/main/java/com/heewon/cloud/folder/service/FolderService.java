package com.heewon.cloud.folder.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.repository.FileInfoRepository;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.dto.FileInfoResponse;
import com.heewon.cloud.folder.dto.FolderInfoResponse;
import com.heewon.cloud.folder.dto.FolderRenameRequest;
import com.heewon.cloud.folder.repository.FolderInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {

	private final FolderInfoRepository folderInfoRepository;
	private final FileInfoRepository fileInfoRepository;
	private final String thumbnailPath = System.getenv("ROOT_PATH") + "thumbnail/";
	private final Long maxSize = 20L * 1024 * 1024 * 1024;

	@Transactional
	public void renameFolder(FolderRenameRequest renameRequest) {
		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			renameRequest.getUserInfo(),
			renameRequest.getOriginalName());
		if (folderInfo == null) {
			throw new NotFoundException("폴더가 존재하지 않습니다.");
		}
		folderInfo.setFolderName(renameRequest.getNewName());
	}

	@Transactional
	public void saveFolder(String rootName, String name, String userInfo) {
		FolderInfo parentInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			userInfo, "~");
		if (parentInfo == null) {
			parentInfo = FolderInfo.builder()
				.folderName("~")
				.parentFolder(null)
				.childrenFolder(new ArrayList<>())
				.fileInfoList(new ArrayList<>())
				.fileCnt(0)
				.folderSize(0L)
				.folderCnt(0)
				.userInfo(userInfo)
				.build();
			folderInfoRepository.save(parentInfo);
		}

		String[] folders = rootName.split("/");

		if (rootName.equals("")) {
			for (FolderInfo folderInfo : parentInfo.getChildrenFolder()) {
				if (folderInfo.getFolderName().equals(name)) {
					throw new FileSystemAlreadyExistsException("이미 존재하는 파일입니다.");
				}
			}

			FolderInfo newFolder = FolderInfo.builder().folderName(name)
				.parentFolder(parentInfo)
				.childrenFolder(new ArrayList<>())
				.fileInfoList(new ArrayList<>())
				.fileCnt(0)
				.folderSize(0L)
				.folderCnt(0)
				.userInfo(userInfo)
				.build();

			folderInfoRepository.save(newFolder);

			parentInfo.getChildrenFolder().add(newFolder);
			parentInfo.calFolderCnt(1);
			return;
		}

		int pos = 0;

		while (pos != folders.length) {

			boolean isExist = false;
			for (FolderInfo info : parentInfo.getChildrenFolder()) {
				if (info.getFolderName().equals(folders[pos])) {
					isExist = true;
					parentInfo = info;
				}
			}

			if (!isExist) {
				throw new NotFoundException("존재하지 않는 폴더 입니다.");
			}
			pos++;

		}

		for (FolderInfo folderInfo : parentInfo.getChildrenFolder()) {
			if (folderInfo.getFolderName().equals(name)) {
				throw new FileSystemAlreadyExistsException("이미 존재하는 파일입니다.");
			}
		}

		FolderInfo newFolder = FolderInfo.builder().folderName(name)
			.parentFolder(parentInfo)
			.childrenFolder(new ArrayList<>())
			.fileInfoList(new ArrayList<>())
			.fileCnt(0)
			.folderSize(0L)
			.folderCnt(0)
			.userInfo(userInfo)
			.build();

		folderInfoRepository.save(newFolder);
		parentInfo.getChildrenFolder().add(newFolder);

		do {
			parentInfo.calFolderCnt(1);
			parentInfo = parentInfo.getParentFolder();
		} while (parentInfo != null);

	}

	public FolderInfoResponse findFolderInfo(String userInfo, String path) {
		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			userInfo, "~");

		String[] folderPath = path.split("/");

		for (String folder : folderPath) {
			for (FolderInfo info : folderInfo.getChildrenFolder()) {
				if (info.getFolderName().equals(folder)) {
					folderInfo = info;
					break;
				}
			}
		}
		int folderSize = folderInfo.getChildrenFolder().size();
		int fileSize = Math.min(100 - folderSize, folderInfo.getFileInfoList().size());

		return FolderInfoResponse.builder().folderList(
				folderInfo.getChildrenFolder().stream().map(FolderInfo::getFolderName).toList().subList(0, folderSize)
			).fileList(
				folderInfo.getFileInfoList().stream().map(c -> {
					try {
						return findFileInfo(c);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}).toList().subList(0, fileSize)
			).userInfo(userInfo)
			.createdAt(folderInfo.getCreateTime())
			.updatedAt(folderInfo.getUpdateTime())
			.path(path)
			.build();
	}

	public FileInfoResponse findFileInfo(FileInfo fileInfo) throws UnsupportedEncodingException {
		String saveFileName = null;
		if (fileInfo.getType().equals("jpg") || fileInfo.getType().equals("png") || fileInfo.getType().equals("jpeg")) {
			saveFileName = thumbnailPath + fileInfo.getIdentifier() + "." + fileInfo.getType();
			if (!Files.exists(Path.of(saveFileName))) {
				saveFileName = null;
			}
		}

		return FileInfoResponse.builder()
			.fileType(fileInfo.getType())
			.updatedAt(fileInfo.getUpdateTime())
			.fileName(URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8))
			.thumbnailPath(saveFileName)
			.build();
	}

	public FolderInfo findFolder(String userInfo, String path) {

		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			userInfo,
			"~");

		String[] pathDir = path.split("/");

		for (String dir : pathDir) {
			for (FolderInfo child : folderInfo.getChildrenFolder()) {
				if (child.getFolderName().equals(dir)) {
					folderInfo = child;
				}
			}
		}

		return folderInfo;

	}

	public FolderInfo findFolder(String userInfo, String path, Long fileSize) {

		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			userInfo,
			"~");

		if (folderInfo.getFolderSize() + fileSize > maxSize) {
			throw new OutOfMemoryError("너무 큼");
		}

		String[] pathDir = path.split("/");

		for (String dir : pathDir) {
			boolean check = false;
			for (FolderInfo child : folderInfo.getChildrenFolder()) {
				if (child.getFolderName().equals(dir)) {
					folderInfo = child;
					check = true;
					break;
				}
			}
			if (!check) {
				throw new NotFoundException("경로가 잘못되었습니다.");
			}
		}

		return folderInfo;

	}

	@Transactional
	public void deleteFolder(String userInfo, String path) {
		FolderInfo folderInfo = findFolder(userInfo, path);
		FolderInfo parent = folderInfo.getParentFolder();
		int folderCnt = folderInfo.getFolderCnt();
		int fileCnt = folderInfo.getFileCnt();
		Long folderSize = folderInfo.getFolderSize();

		while (parent != null) {
			parent.calFolderSize(-folderSize);
			parent.calFileCnt(-fileCnt - 1);
			parent.calFolderCnt(-folderCnt);
			parent = parent.getParentFolder();
		}

		folderInfoRepository.delete(folderInfo);
	}

	@Transactional
	public void moveFolder(String userInfo, String oldPath, String newPath, String folderName) {
		FolderInfo oldFolder = null;
		FolderInfo newFolder = null;

		newFolder = findFolder(userInfo, newPath);
		oldFolder = findFolder(userInfo, oldPath);

		if (oldFolder == null) {
			System.out.println("old folder is null");
		}
		if (newFolder == null) {
			System.out.println("new folder is null");
		}

		FolderInfo curr = null;

		for (FolderInfo info : newFolder.getChildrenFolder()) {
			if (info.getFolderName().equals(folderName)) {
				throw new FileSystemAlreadyExistsException("이미 존재하는 파일입니다.");
			}
		}

		for (FolderInfo info : oldFolder.getChildrenFolder()) {
			if (info.getFolderName().equals(folderName)) {
				curr = info;
				break;
			}
		}

		if (curr == null) {
			throw new NotFoundException("존재하지 않는 폴더입니다.");
		}

		oldFolder.getChildrenFolder().remove(curr);

		Long folderSize = curr.getFolderSize();
		int folderCnt = curr.getFolderCnt() + 1;
		int fileCnt = curr.getFileCnt();

		while (true) {
			oldFolder.calFolderSize(-folderSize);
			oldFolder.calFolderCnt(-folderCnt);
			oldFolder.calFileCnt(-fileCnt);
			if (oldFolder.getParentFolder() == null)
				break;
			oldFolder = oldFolder.getParentFolder();
		}

		curr.setParentFolder(newFolder);
		newFolder.getChildrenFolder().add(curr);

		while (true) {
			newFolder.calFolderSize(folderSize);
			newFolder.calFolderCnt(folderCnt);
			newFolder.calFileCnt(fileCnt);
			if (newFolder.getParentFolder() == null)
				break;
			newFolder = newFolder.getParentFolder();
		}

	}
}
