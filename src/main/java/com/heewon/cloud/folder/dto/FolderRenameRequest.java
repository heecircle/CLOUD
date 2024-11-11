package com.heewon.cloud.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FolderRenameRequest {
	private String originalName;
	private String newName;
	private String userInfo;
}
