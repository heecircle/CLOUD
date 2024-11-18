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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LinkInfo extends BaseTimeEntity {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	// @Getter
	// @GeneratedValue(strategy = GenerationType.UUID)
	// private String url;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = true)
	private FolderInfo folder;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = true)
	private FileInfo file;

	@Override
	public String toString() {
		String folderName = "";
		if (folder != null) {
			folderName = folder.getFolderName();
		}

		String fileName = "";
		if (file != null) {
			fileName = file.getName();
		}

		return id + " " + folderName + " " + fileName;
	}
}
