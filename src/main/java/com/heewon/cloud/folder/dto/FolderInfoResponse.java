package com.heewon.cloud.folder.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FolderInfoResponse {
	List<String> folderList;
	List<String> fileList;

	LocalDateTime createdAt;
	LocalDateTime updatedAt;

	String path;
	String userInfo;
}
