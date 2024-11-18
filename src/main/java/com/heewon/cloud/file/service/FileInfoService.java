package com.heewon.cloud.file.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import net.coobird.thumbnailator.Thumbnails;

import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.dto.FileMoveRequest;
import com.heewon.cloud.file.repository.FileInfoRepository;
import com.heewon.cloud.folder.domain.FolderInfo;
import com.heewon.cloud.folder.repository.FolderInfoRepository;
import com.heewon.cloud.folder.service.FolderService;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileInfoService {
	private final FileInfoRepository fileInfoRepository;
	private final FolderService folderService;
	private final String rootPath = System.getenv("ROOT_PATH");
	private final String thumbnailPath = rootPath + "thumbnail/";
	private final FolderInfoRepository folderInfoRepository;

	public String fileType(String type) {

		return switch (type) {
			case "png" -> MediaType.IMAGE_PNG_VALUE;
			case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
			case "gif" -> MediaType.IMAGE_GIF_VALUE;
			default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
		};

	}

	public FileInfo getFileInfo(String userInfo, String fileName) {
		return fileInfoRepository.findFileInfoByNameAndUserInfo(fileName, userInfo);
	}

	@Transactional
	public void fileSave(MultipartFile file, String userInfo, String rootPath, String savePath) throws
		IOException,
		NotSupportedException {

		Long fileSize = file.getSize();

		FolderInfo folderInfo = folderService.findFolder(userInfo, savePath, fileSize);
		if (folderInfo == null) {
			throw new NotFoundException("존재하지 않는 폴더~");
		}
		fileSaveApply(folderInfo, rootPath, file);

	}

	public void fileSaveApply(FolderInfo folderInfo, String rootPath, MultipartFile file) throws
		IOException, NotSupportedException {
		Long fileSize = file.getSize();
		String fileName = file.getOriginalFilename();
		String type = "";

		if (file.isEmpty() || file.getSize() <= 0) {
			throw new NotSupportedException("파일의 크기가 너무 작습니다.");
		}

		try {
			assert fileName != null;
			type = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (NullPointerException e) {
			System.out.println("확장자 명이 없습니다.");
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
			.userInfo(folderInfo.getUserInfo())
			.folderInfo(folderInfo)
			.build();

		fileInfoRepository.save(saveFile);

		folderInfo.getFileInfoList().add(saveFile);

		String fileDescriptor = saveFile.getIdentifier();

		if (!file.isEmpty() && file.getSize() > 0) {
			String fullPath = rootPath + fileDescriptor + "." + type;
			File imageFile = new File(fullPath);
			if (imageFile.exists()) {
				throw new FileAlreadyExistsException("이미 존재하는 파일입니다.");
			}
			file.transferTo(new File(fullPath));

			String thumbnailPathResult = thumbnailPath + fileDescriptor + "." + type;
			File thumbnailFile = new File(thumbnailPathResult);
			if (thumbnailFile.exists()) {
				throw new FileAlreadyExistsException("이미 존재하는 파일입니다.");
			}
			BufferedImage boImg = ImageIO.read(imageFile);
			double ratio = 3;
			int width = (int)(boImg.getWidth() / ratio);
			int height = (int)(boImg.getHeight() / ratio);

			Thumbnails.of(imageFile).size(width, height).toFile(thumbnailFile);

		}

		while (folderInfo != null) {
			folderInfo.calFileCnt(1);
			folderInfo.calFolderSize(fileSize);
			folderInfoRepository.save(folderInfo);
			folderInfo = folderInfo.getParentFolder();
		}
	}

	public FileGetResponse getFile(String userInfo, String fileName, String folderName) throws
		UnsupportedEncodingException {
		FolderInfo folderInfo = folderService.findFolder(userInfo, folderName);
		if (folderInfo == null) {
			throw new NotFoundException("존재하지 않는 폴더");
		}

		return getFileApply(folderInfo, fileName);
	}

	public FileGetResponse getFileApply(FolderInfo folderInfo, String fileName) throws UnsupportedEncodingException {
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

	/**
	 * 파일 옮기기
	 * @param fileMoveRequest
	 */

	@Transactional
	public void fileMove(FileMoveRequest fileMoveRequest) {
		FolderInfo folderInfo = folderService.findFolder(fileMoveRequest.getUserInfo(), fileMoveRequest.getFrom());
		FolderInfo getNextFolder = folderService.findFolder(fileMoveRequest.getUserInfo(), fileMoveRequest.getTo());
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

		if (fileInfo == null) {
			throw new NotFoundException("파일이 존재하지 않습니다.");
		}
		fileMoveApply(folderInfo, getNextFolder, fileInfo);
	}

	@Transactional
	public void fileMoveApply(FolderInfo folderInfo, FolderInfo getNextFolder, FileInfo fileInfo) {
		folderInfo.getFileInfoList().remove(fileInfo);

		getNextFolder.getFileInfoList().add(fileInfo);
		fileInfo.setParentFolder(getNextFolder);

		while (folderInfo != null) {
			folderInfo.calFileCnt(-1);
			folderInfoRepository.save(folderInfo);
			folderInfo = folderInfo.getParentFolder();
		}

		while (getNextFolder != null) {
			getNextFolder.calFileCnt(1);
			folderInfoRepository.save(getNextFolder);
			getNextFolder = getNextFolder.getParentFolder();
		}
	}

	/**
	 * 파일 삭제
	 * @param userInfo
	 * @param fileName
	 * @param folderName
	 */

	public void fileDelete(String userInfo, String fileName, String folderName) {
		FolderInfo folderInfo = folderService.findFolder(userInfo, folderName);
		if (folderInfo == null) {
			throw new NotFoundException("존재하지 않는 폴더입니다.");
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
		fileDeleteApply(folderInfo, fileInfo);

	}

	public void fileDeleteApply(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.getFileInfoList().remove(fileInfo);

		while (folderInfo != null) {
			folderInfo.calFileCnt(-1);
			folderInfoRepository.save(folderInfo);
			folderInfo = folderInfo.getParentFolder();
		}
		String filePath = rootPath + fileInfo.getIdentifier() + "." + fileInfo.getType();
		String thumbnailPath_ = thumbnailPath + fileInfo.getIdentifier() + "." + fileInfo.getType();
		try {
			Files.delete(Path.of(filePath));
			Files.delete(Path.of(thumbnailPath_));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		fileInfoRepository.delete(fileInfo);
	}

}
