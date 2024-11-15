package com.heewon.cloud.link.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.service.FileInfoService;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.link.domain.LinkInfo;
import com.heewon.cloud.link.repository.LinkInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileLinkService {
	private final LinkInfoRepository linkInfoRepository;
	private final FileInfoService fileInfoService;
	private final String rootPath = System.getenv("ROOT_PATH");

	public FileGetResponse getFile(LinkInfo linkInfo) throws UnsupportedEncodingException {
		FileInfo fileInfo = linkInfo.getFile();
		String saveFileName = rootPath + fileInfo.getIdentifier() + "." + fileInfo.getType();
		String originalFileName = URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8);

		Resource resource = new FileSystemResource(
			saveFileName);

		return FileGetResponse.builder()
			.fileName(originalFileName)
			.fileSize(fileInfo.getSize())
			.type(fileInfoService.fileType(fileInfo.getType()))
			.resource(resource).build();
	}

	public void fileMake(LinkInfo linkInfo, MultipartFile file) throws IOException {
		FolderInfo folderInfo = linkInfo.getFolder();
		fileInfoService.fileSaveApply(folderInfo, rootPath, file);
	}

	public void fileDelete(LinkInfo linkInfo, String fileName) throws IOException {
		FolderInfo folderInfo = linkInfo.getFolder();
		FileInfo fileInfo = null;
		for (FileInfo info : linkInfo.getFolder().getFileInfoList()) {
			if (info.getIdentifier().equals(fileName)) {
				fileInfo = info;
				break;
			}
		}
		fileInfoService.fileDeleteApply(folderInfo, fileInfo);
	}

	@Transactional
	public void fileRename(LinkInfo linkInfo, String oldName, String newName) throws IOException {
		
		FileInfo fileInfo = null;
		for (FileInfo info : linkInfo.getFolder().getFileInfoList()) {
			if (info.getIdentifier().equals(oldName)) {
				fileInfo = info;
				break;
			}
		}
		if (fileInfo == null) {
			throw new NotFoundException("존재 안하는 파일");
		}
		fileInfo.setName(newName);

	}
}
