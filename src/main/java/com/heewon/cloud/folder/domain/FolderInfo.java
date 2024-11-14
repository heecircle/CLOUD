package com.heewon.cloud.folder.domain;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.heewon.cloud.common.base.BaseTimeEntity;
import com.heewon.cloud.file.domain.FileInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
@Entity
public class FolderInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String folderIdentifier;

	@Column
	private String userInfo;

	@Column
	@Setter
	private String folderName;

	@Setter
	@JoinColumn
	@ManyToOne
	private FolderInfo parentFolder;

	@JoinColumn
	@OneToMany(cascade = CascadeType.REMOVE)
	private List<FolderInfo> childrenFolder;

	@JoinColumn
	@OneToMany(cascade = CascadeType.REMOVE)
	private List<FileInfo> fileInfoList;

	@ColumnDefault("0")
	@Column
	private Long folderSize;

	@ColumnDefault("0")
	@Column
	private Integer folderCnt;

	@ColumnDefault("0")
	@Column
	private Integer fileCnt;

	public void calFolderCnt(int cnt) {
		this.folderCnt += cnt;
	}

	public void calFileCnt(int cnt) {
		this.fileCnt += cnt;
	}

	public void calFolderSize(Long size) {
		this.folderSize += size;
	}

}
