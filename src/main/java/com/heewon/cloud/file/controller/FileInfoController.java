package com.heewon.cloud.file.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.heewon.cloud.file.dto.FileGetResponse;
import com.heewon.cloud.file.dto.FileMoveRequest;
import com.heewon.cloud.file.service.FileInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileInfoController {

	static final String rootPath = System.getenv("ROOT_PATH");
	private final FileInfoService fileInfoService;

	@PostMapping("/save")
	public ResponseEntity<String> save(@RequestPart MultipartFile file, @RequestPart String userInfo,
		@RequestPart String savePath) throws
		IOException {
		fileInfoService.fileSave(file, userInfo, rootPath, savePath);

		return ResponseEntity.ok().body("success");
	}

	@GetMapping("/get")
	public ResponseEntity<Resource> get(@RequestParam String fileName, @RequestParam String userInfo,
		@RequestParam String rootPath) throws
		UnsupportedEncodingException {
		FileGetResponse fileInfoResponse = fileInfoService.getFile(userInfo, fileName, rootPath);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfoResponse.getFileName() + "\"")
			.header(HttpHeaders.CONTENT_TYPE, fileInfoResponse.getType())
			.body(fileInfoResponse.getResource());
	}

	@PatchMapping("/move")
	public void move(@RequestBody FileMoveRequest fileMoveRequest) {
		fileInfoService.fileMove(fileMoveRequest);
	}
}
