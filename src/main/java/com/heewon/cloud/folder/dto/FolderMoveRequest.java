package com.heewon.cloud.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FolderMoveRequest {
	private String from;
	private String to;
	private String folderName;
	private String userInfo;
}
