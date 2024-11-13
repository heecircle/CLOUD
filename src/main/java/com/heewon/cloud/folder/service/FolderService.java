package com.heewon.cloud.folder.service;

import java.nio.file.FileSystemAlreadyExistsException;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.repository.FileInfoRepository;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.dto.FolderInfoResponse;
import com.heewon.cloud.folder.dto.FolderRenameRequest;
import com.heewon.cloud.folder.repository.FolderInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {

	private final FolderInfoRepository folderInfoRepository;
	private final FileInfoRepository fileInfoRepository;

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
				.folderSize(0)
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
				.folderSize(0)
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
			.folderSize(0)
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

		return FolderInfoResponse.builder().folderList(
				folderInfo.getChildrenFolder().stream().map(FolderInfo::getFolderName).toList()
			).fileList(
				folderInfo.getFileInfoList().stream().map(FileInfo::getName).toList()
			).userInfo(userInfo)
			.createdAt(folderInfo.getCreateTime())
			.updatedAt(folderInfo.getUpdateTime())
			.path(path)
			.build();
	}

	public FolderInfo findFolderRoot(String userInfo, String path) {

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

	@Transactional
	public void deleteFolder(String userInfo, String path) {
		FolderInfo folderInfo = findFolderRoot(userInfo, path);
		FolderInfo parent = folderInfo.getParentFolder();
		int folderCnt = folderInfo.getFolderCnt();
		int fileCnt = folderInfo.getFileCnt();
		int folderSize = folderInfo.getFolderSize();

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

		newFolder = findFolderRoot(userInfo, newPath);
		oldFolder = findFolderRoot(userInfo, oldPath);

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

		int folderSize = curr.getFolderSize();
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
