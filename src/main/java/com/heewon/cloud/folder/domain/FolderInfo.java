package com.heewon.cloud.folder.domain;

import java.util.List;

import com.heewon.cloud.file.domain.FileInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FolderInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String folderIdentifier;

	@Column
	private String userInfo;

	@Column
	@Setter
	private String folderName;

	@JoinColumn
	@ManyToOne
	private FolderInfo parentFolder;

	@JoinColumn
	@OneToMany(cascade = CascadeType.REMOVE)
	private List<FolderInfo> childrenFolder;

	@JoinColumn
	@OneToMany(cascade = CascadeType.REMOVE)
	private List<FileInfo> fileInfoList;

}
