package com.heewon.cloud.folder.service;

import org.springframework.stereotype.Service;

import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.repository.FolderInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderCommonService {
	private final FolderInfoRepository folderInfoRepository;

	FolderInfo findRootFolderUser(String userInfo, String folderPath) {
			
		return null;
	}

}
