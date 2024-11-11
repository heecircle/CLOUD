package com.heewon.cloud.folder.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.dto.FolderRenameRequest;
import com.heewon.cloud.folder.dto.FolderSaveRequest;
import com.heewon.cloud.folder.repository.FolderInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderInfoRepository folderInfoRepository;

	public void saveFolder(FolderSaveRequest folderSaveRequest) {

		FolderInfo folderInfo = FolderInfo.builder()
			.childrenFolder(new ArrayList<>())
			.fileInfoList(new ArrayList<>())
			.userInfo(folderSaveRequest.getUserInfo())
			.parentFolder(null)
			.folderName(folderSaveRequest.getFolderName())
			.userInfo(folderSaveRequest.getUserInfo())
			.build();

		folderInfoRepository.save(folderInfo);

		System.out.println(folderInfo.getFolderIdentifier());
	}

	@Transactional
	public void renameFolder(FolderRenameRequest renameRequest) {
		FolderInfo folderInfo = folderInfoRepository.findFolderInfoByUserInfoAndFolderName(renameRequest.getUserInfo(),
			renameRequest.getOriginalName());
		if (folderInfo == null) {
			throw new NotFoundException("폴더가 존재하지 않습니다.");
		}
		folderInfo.setFolderName(renameRequest.getNewName());
	}
}
