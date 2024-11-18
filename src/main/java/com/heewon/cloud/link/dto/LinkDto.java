package com.heewon.cloud.link.dto;

import com.heewon.cloud.link.domain.LinkInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto<T> {
	private String link;

	@Setter
	private LinkInfo linkInfo;
	private T data;
}

