package com.heewon.cloud.link.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heewon.cloud.link.common.LinkAvailable;
import com.heewon.cloud.link.dto.LinkCreateFileRequestDto;
import com.heewon.cloud.link.service.LinkInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkInfoController {
	private final LinkInfoService linkInfoService;

	@PostMapping
	public String makeLinkInfo(@RequestBody LinkCreateFileRequestDto linkCreateFileRequestDto) {
		return linkInfoService.makeFileLink(linkCreateFileRequestDto.getUserInfo(),
			linkCreateFileRequestDto.getFilePath(),
			linkCreateFileRequestDto.getFileName());
	}

	public String makeFolderWithLinkInfo(@RequestBody LinkCreateFileRequestDto linkCreateFileRequestDto) {

		return "";
	}

	@LinkAvailable
	@GetMapping("/file/download")
	public void downloadFileWithLink(@RequestParam String fileLink) {

	}

}
