package com.heewon.cloud.folder.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heewon.cloud.folder.dto.FolderInfoResponse;
import com.heewon.cloud.folder.dto.FolderMoveRequest;
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
		folderService.saveFolder(
			folderSaveRequest.getRootFolder(),
			folderSaveRequest.getFolderName(),
			folderSaveRequest.getUserInfo());
	}

	@PatchMapping("/rename")
	public void renameFolder(@RequestBody FolderRenameRequest folderRenameRequest) {
		folderService.renameFolder(folderRenameRequest);
	}

	@GetMapping("/info")
	public FolderInfoResponse getFolderInfo(@RequestParam String folderName, @RequestParam String userName) {
		return folderService.findFolderInfo(userName, folderName);
	}

	@DeleteMapping("/delete")
	public void deleteFolder(@RequestParam String folderName, @RequestParam String userName) {
		folderService.deleteFolder(userName, folderName);
	}

	@PatchMapping("/move")
	public void moveFolder(@RequestBody FolderMoveRequest folderMoveRequest) {
		folderService.moveFolder(folderMoveRequest.getUserInfo(), folderMoveRequest.getFrom(),
			folderMoveRequest.getTo(),
			folderMoveRequest.getFolderName());
	}

}
