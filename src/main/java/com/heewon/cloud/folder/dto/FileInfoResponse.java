package com.heewon.cloud.folder.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoResponse {
	String fileName;
	String fileType;
	String thumbnailPath;
	LocalDateTime updatedAt;

}
