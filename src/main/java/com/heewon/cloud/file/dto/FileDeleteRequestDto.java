package com.heewon.cloud.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileDeleteRequestDto {
	
	private String fileName;
	private String filePath;
	private String userInfo;

}
