package com.heewon.cloud.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileMoveRequest {

	private String from;

	private String to;

	private String fileName;

	private String userInfo;

}
