package com.heewon.cloud.folder.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heewon.cloud.folder.dto.FolderRenameRequest;
import com.heewon.cloud.folder.dto.FolderSaveRequest;
import com.heewon.cloud.folder.service.FolderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderInfoController {

	private final FolderService folderService;

	@PostMapping("/save")
	public void makeFolder(@RequestBody FolderSaveRequest folderSaveRequest) {
		folderService.saveFolder(folderSaveRequest);
	}

	@PatchMapping("/rename")
	public void renameFolder(@RequestBody FolderRenameRequest folderRenameRequest) {
		folderService.renameFolder(folderRenameRequest);
	}

}
