package com.heewon.cloud.file.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.repository.FileInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileInfoService {
	private final FileInfoRepository fileInfoRepository;
	private final String rootPath = System.getenv("ROOT_PATH");

	public FileInfo getFileInfo(String userInfo, String fileName) {
		return fileInfoRepository.findFileInfoByNameAndUserInfo(fileName, userInfo);
	}

	public void fileSave(MultipartFile file, String userInfo, String rootPath) throws IOException {

		String fileName = file.getOriginalFilename();
		FileInfo existFile = getFileInfo(userInfo, fileName);
		String type = "";
		try {
			assert fileName != null;
			type = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (NullPointerException e) {
			System.out.println("확장자 명이 없습니다.");
		}

		if (existFile != null) {
			throw new IOException("이미 존재하는 파일입니다.");
		}

		FileInfo saveFile = FileInfo.builder()
			.name(fileName)
			.size(file.getSize())
			.type(type)
			.userInfo(userInfo)
			.file(fileName)
			.build();

		fileInfoRepository.save(saveFile);

		String fileDescriptor = saveFile.getIdentifier();

		if (!file.isEmpty() && file.getSize() > 0) {
			String fullPath = rootPath + fileDescriptor + "." + type;
			file.transferTo(new File(fullPath));
		}

	}

	public FileGetResponse getFile(String userInfo, String fileName) throws UnsupportedEncodingException {
		FileInfo fileInfo = getFileInfo(userInfo, fileName);
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

		if (type.equals("png")) {
			return MediaType.IMAGE_PNG_VALUE;
		}

		if (type.equals("jpg") || type.equals("jpeg")) {
			return MediaType.IMAGE_JPEG_VALUE;
		}

		if (type.equals("gif")) {
			return MediaType.IMAGE_GIF_VALUE;
		}

		return MediaType.APPLICATION_OCTET_STREAM_VALUE;

	}

}
