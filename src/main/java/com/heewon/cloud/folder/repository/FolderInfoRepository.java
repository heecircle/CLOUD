package com.heewon.cloud.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heewon.cloud.folder.domain.FolderInfo;

@Repository
public interface FolderInfoRepository extends JpaRepository<FolderInfo, Long> {
	FolderInfo findFolderInfoByUserInfoAndFolderNameAndParentFolderIsNull(String userInfo, String folderName);
}
