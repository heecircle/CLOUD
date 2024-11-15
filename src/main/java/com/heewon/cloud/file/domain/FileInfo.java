package com.heewon.cloud.file.domain;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.heewon.cloud.common.base.BaseTimeEntity;
import com.heewon.cloud.folder.domain.FolderInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@Column
	private Long size;

	@Setter
	@Column(nullable = false)
	private String name; // 파일 이름

	@Column
	private String type; // 파일 종류

	@Setter
	@JoinColumn
	@ManyToOne
	private FolderInfo parentFolder; // 파일

	@Builder
	public FileInfo(String userInfo, Long size, String name, String type, FolderInfo folderInfo) {
		this.userInfo = userInfo;
		this.size = size;
		this.name = name;
		this.type = type;
		this.parentFolder = folderInfo;
	}
}
