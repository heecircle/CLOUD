package com.heewon.cloud.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FolderSaveRequest {
	private String folderName;
	private String userInfo;
}
