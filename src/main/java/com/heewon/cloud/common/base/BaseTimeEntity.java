package com.heewon.cloud.common.base;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public class BaseTimeEntity {

	@CreatedDate
	private LocalDateTime createTime;

	@LastModifiedDate
	private LocalDateTime updateTime;

}
