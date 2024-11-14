package com.heewon.cloud.link.service;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.service.FolderService;
import com.heewon.cloud.link.repository.LinkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {
	private final LinkRepository fileLInkRepository;
	private final FolderService folderService;

	public void makeFileLink(String userInfo, String filePath, String fileName) {
		FolderInfo folderInfo = folderService.findFolderRoot(userInfo, filePath);

		if (folderInfo == null)
			throw new NotFoundException("존재하지 않는 파일입니다.");
		FileInfo fileInfo = null;
		for (FileInfo info : folderInfo.getFileInfoList()) {
			if (info.getName().equals(fileName)) {
				fileInfo = info;
			}
		}

		if (fileInfo == null) {
			throw new NotFoundException("존재하지 않는 파일입니다.");
		}

	}
}
