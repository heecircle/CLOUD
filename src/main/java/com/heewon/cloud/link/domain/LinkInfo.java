package com.heewon.cloud.link.domain;

import com.heewon.cloud.common.base.BaseTimeEntity;
import com.heewon.cloud.file.domain.FileInfo;
import com.heewon.cloud.folder.domain.FolderInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Link extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private Long id;

	private String url;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = true)
	private FolderInfo folder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = true)
	private FileInfo file;

}
