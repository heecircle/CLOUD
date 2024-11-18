package com.heewon.cloud.link.controller;

import java.io.IOException;
import java.security.cert.CertificateExpiredException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.heewon.cloud.file.dto.FileDeleteRequestDto;
import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.dto.FileRenameRequestDto;
import com.heewon.cloud.link.common.LinkAvailable;
import com.heewon.cloud.link.dto.LinkCreateFileRequestDto;
import com.heewon.cloud.link.dto.LinkCreateFolderRequestDto;
import com.heewon.cloud.link.dto.LinkDto;
import com.heewon.cloud.link.service.FileLinkService;
import com.heewon.cloud.link.service.LinkInfoService;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkInfoController {
	private final LinkInfoService linkInfoService;
	private final FileLinkService fileLinkService;

	@PostMapping("/filelink")
	public String makeLinkFileInfo(@RequestBody LinkCreateFileRequestDto linkCreateFileRequestDto) {
		return linkInfoService.makeFileLink(linkCreateFileRequestDto.getUserInfo(),
			linkCreateFileRequestDto.getFilePath(),
			linkCreateFileRequestDto.getFileName());
	}

	@GetMapping("/file/download")
	public ResponseEntity<?> downloadFileWithLink(@RequestParam String fileLink) throws
		CertificateExpiredException {

		FileGetResponse fileInfoResponse = fileLinkService.getFile(fileLink);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfoResponse.getFileName() + "\"")
			.header(HttpHeaders.CONTENT_TYPE, fileInfoResponse.getType())
			.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileInfoResponse.getFileSize()))
			.body(fileInfoResponse.getResource());
	}

	@PostMapping("/folderlink")
	public String makeLinkFolderInfo(@RequestBody LinkCreateFolderRequestDto linkCreateFolderRequestDto) {
		return linkInfoService.makeFolderLink(linkCreateFolderRequestDto.getUserInfo(),
			linkCreateFolderRequestDto.getFolderPath());
	}

	@LinkAvailable
	@PostMapping("/file/upload")
	public void uploadFileWithLink(@RequestPart LinkDto<String> linkDto, @RequestPart MultipartFile file) throws
		IOException, NotSupportedException {
		fileLinkService.fileMake(linkDto.getLinkInfo(), file);
	}

	@LinkAvailable
	@DeleteMapping("/file/delete")
	public void deleteFileWithLink(@RequestBody LinkDto<FileDeleteRequestDto> linkDto) throws IOException {
		fileLinkService.fileDelete(linkDto.getLinkInfo(), linkDto.getData().getFileName());
	}

	@LinkAvailable
	@PatchMapping("/file/rename")
	public void renameFileWithLink(@RequestBody LinkDto<FileRenameRequestDto> linkDto) throws IOException {
		fileLinkService.fileRename(linkDto.getLinkInfo(), linkDto.getData().getOldName(),
			linkDto.getData().getNewName());
	}

}
