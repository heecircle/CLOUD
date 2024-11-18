package com.heewon.cloud.link.service;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.service.FolderService;
import com.heewon.cloud.link.domain.LinkInfo;
import com.heewon.cloud.link.repository.LinkInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkInfoService {
	private final LinkInfoRepository linkInfoRepository;
	private final FolderService folderService;

	public String makeFileLink(String userInfo, String filePath, String fileName) {
		FolderInfo folderInfo = folderService.findFolder(userInfo, filePath);

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
		LinkInfo linkInfo = LinkInfo.builder().file(fileInfo).build();
		linkInfoRepository.save(linkInfo);
		return linkInfo.getId();
	}

	public String makeFolderLink(String userInfo, String folderPath) {
		FolderInfo folderInfo = folderService.findFolder(userInfo, folderPath);
		LinkInfo linkInfo = LinkInfo.builder().folder(folderInfo).build();
		linkInfoRepository.save(linkInfo);
		return linkInfo.getId();
	}
}
