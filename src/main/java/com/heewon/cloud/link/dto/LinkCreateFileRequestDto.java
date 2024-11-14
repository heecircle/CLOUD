package com.heewon.cloud.link.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkCreateFileRequestDto {
	private String fileName;
	private String filePath;
	private String userInfo;

}
