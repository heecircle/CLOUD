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
	public void saveFolder(String rootName, String userInfo) {
		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(
			userInfo, "~");
		if (folderInfo == null) {
			folderInfo = FolderInfo.builder()
				.folderName("~")
				.parentFolder(null)
				.childrenFolder(new ArrayList<>())
				.fileInfoList(new ArrayList<>())
				.userInfo(userInfo)
				.build();
			folderInfoRepository.save(folderInfo);
		}

		String[] folders = rootName.split("/");

		if (folders.length == 0) {
			folderInfo.getChildrenFolder().add(folderInfo);
		}

		int pos = 0;
		while (pos != folders.length) {

			boolean isExist = false;
			for (FolderInfo info : folderInfo.getChildrenFolder()) {
				if (info.getFolderName().equals(folders[pos])) {
					isExist = true;
					folderInfo = info;
				}
			}

			if (!isExist) {

				FolderInfo nextFolderInfo = FolderInfo.builder()
					.parentFolder(folderInfo)
					.folderName(folders[pos])
					.userInfo(userInfo)
					.childrenFolder(new ArrayList<>())
					.fileInfoList(new ArrayList<>())
					.build();

				folderInfoRepository.save(nextFolderInfo);

				folderInfo.getChildrenFolder().add(nextFolderInfo);

				folderInfo = nextFolderInfo;

			}
			pos++;

		}

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
		folderInfoRepository.delete(folderInfo);
	}

	public void moveFolder(String userInfo, String oldPath, String newPath, String folderName) {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(userInfo + " " + oldPath + " " + newPath + " " + folderName);
		FolderInfo oldFolder = null;
		FolderInfo newFolder = null;

		newFolder = findFolderRoot(userInfo, newPath);
		oldFolder = findFolderRoot(userInfo, oldPath);
		// System.out.println(oldFolder + " " + newFolder);

		if (oldFolder == null) {
			System.out.println("old folder is null");
		}
		if (newFolder == null) {
			System.out.println("new folder is null");
		}

		// if (oldFolder == null || newFolder == null) {
		// 	throw new NotFoundException("존재하지 않는 폴더입니다.");
		// }

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

		oldFolder.getChildrenFolder().remove(curr);

		if (curr == null) {
			throw new NotFoundException("존재하지 않는 폴더입니다.");
		}

		curr.setParentFolder(newFolder);
		newFolder.getChildrenFolder().add(curr);

		folderInfoRepository.save(curr);
		folderInfoRepository.save(oldFolder);
		folderInfoRepository.save(newFolder);

	}
}
