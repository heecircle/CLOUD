package com.heewon.cloud.file.domain;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.heewon.cloud.common.base.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@EnableJpaAuditing(setDates = true)
public class FileInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String identifier;

	@Column(nullable = false)
	private String userInfo;

	// @Column
	// 파일 위치

	@Column
	private Long size;

	@Column(nullable = false)
	private String name; // 파일 이름

	@Column
	private String type; // 파일 종류

	@Column(nullable = true)
	private String file; // 파일

	@Builder
	public FileInfo(String userInfo, Long size, String name, String type, String file) {
		this.userInfo = userInfo;
		this.size = size;
		this.name = name;
		this.type = type;
		this.file = file;
	}
}
