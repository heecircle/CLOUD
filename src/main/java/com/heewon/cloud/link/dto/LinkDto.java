package com.heewon.cloud.link.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto<T> {
	private String link;
	private T requestBody;
}
