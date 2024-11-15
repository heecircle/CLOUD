package com.heewon.cloud.link.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkCreateFolderRequestDto {
	private String folderPath;
	private String userInfo;

}
