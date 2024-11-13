package com.heewon.cloud.file.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemAlreadyExistsException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.dto.FileMoveRequest;
import com.heewon.cloud.file.repository.FileInfoRepository;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.service.FolderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileInfoService {
	private final FileInfoRepository fileInfoRepository;
	private final FolderService folderService;
	private final String rootPath = System.getenv("ROOT_PATH");

	public FileInfo getFileInfo(String userInfo, String fileName) {
		return fileInfoRepository.findFileInfoByNameAndUserInfo(fileName, userInfo);
	}

	@Transactional
	public void fileSave(MultipartFile file, String userInfo, String rootPath, String savePath) throws IOException {

		String fileName = file.getOriginalFilename();
		FolderInfo folderInfo = folderService.findFolderRoot(userInfo, savePath);

		String type = "";
		try {
			assert fileName != null;
			type = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (NullPointerException e) {
			System.out.println("확장자 명이 없습니다.");
		}

		if (folderInfo == null) {
			throw new NotFoundException("존재하지 않는 폴더입니다.");
		}

		for (FileInfo child : folderInfo.getFileInfoList()) {
			if (child.getName().equals(fileName)) {
				throw new FileSystemAlreadyExistsException("이미 존재하는 파일입니다.");
			}
		}

		FileInfo saveFile = FileInfo.builder()
			.name(fileName)
			.size(file.getSize())
			.type(type)
			.userInfo(userInfo)
			.folderInfo(folderInfo)
			.build();

		fileInfoRepository.save(saveFile);

		folderInfo.getFileInfoList().add(saveFile);

		String fileDescriptor = saveFile.getIdentifier();

		if (!file.isEmpty() && file.getSize() > 0) {
			String fullPath = rootPath + fileDescriptor + "." + type;
			file.transferTo(new File(fullPath));
		}

	}

	public FileGetResponse getFile(String userInfo, String fileName, String folderName) throws
		UnsupportedEncodingException {
		FolderInfo folderInfo = folderService.findFolderRoot(userInfo, folderName);

		if (folderInfo == null) {
			throw new NotFoundException("존재하지 않는 폴더 입니다.");
		}
		FileInfo fileInfo = null;
		for (FileInfo child : folderInfo.getFileInfoList()) {
			if (child.getName().equals(fileName)) {
				fileInfo = child;
				break;
			}
		}

		if (fileInfo == null) {
			throw new NotFoundException("존재하지 않는 파일입니다.");
		}

		String saveFileName = rootPath + fileInfo.getIdentifier() + "." + fileInfo.getType();
		String originalFileName = URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8.toString());

		Resource resource = new FileSystemResource(
			saveFileName);

		return FileGetResponse.builder()
			.fileName(originalFileName)
			.fileSize(fileInfo.getSize())
			.type(fileType(fileInfo.getType()))
			.resource(resource).build();
	}

	public String fileType(String type) {

		return switch (type) {
			case "png" -> MediaType.IMAGE_PNG_VALUE;
			case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
			case "gif" -> MediaType.IMAGE_GIF_VALUE;
			default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
		};

	}

	@Transactional
	public void fileMove(FileMoveRequest fileMoveRequest) {
		FolderInfo folderInfo = folderService.findFolderRoot(fileMoveRequest.getUserInfo(), fileMoveRequest.getFrom());
		FolderInfo getNextFolder = folderService.findFolderRoot(fileMoveRequest.getUserInfo(), fileMoveRequest.getTo());
		FileInfo fileInfo = null;

		if (getNextFolder == null) {
			throw new NotFoundException("존재하지 않는 폴더입니다.");
		}

		for (FileInfo child : getNextFolder.getFileInfoList()) {
			if (child.getName().equals(fileMoveRequest.getFileName())) {
				throw new FileSystemAlreadyExistsException("이미 존재하는 파일이름입니다.");
			}
		}

		for (FileInfo child : folderInfo.getFileInfoList()) {
			if (child.getName().equals(fileMoveRequest.getFileName())) {
				fileInfo = child;
				break;
			}
		}

		folderInfo.getFileInfoList().remove(fileInfo);

		if (fileInfo == null) {
			throw new NotFoundException("파일이 존재하지 않습니다.");
		}

		getNextFolder.getFileInfoList().add(fileInfo);
		fileInfo.setParentFolder(getNextFolder);

	}

}
