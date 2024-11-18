package com.heewon.cloud.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileRenameRequestDto {
	private String oldName;
	private String newName;

}
