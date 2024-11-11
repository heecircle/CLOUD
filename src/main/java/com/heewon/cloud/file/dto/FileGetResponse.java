package com.heewon.cloud.file.dto;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileGetResponse {
	private Resource resource;
	private String fileName;
	private Long fileSize;
	private String type;
}
