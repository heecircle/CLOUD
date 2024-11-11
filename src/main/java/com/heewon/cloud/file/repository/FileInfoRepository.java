package com.heewon.cloud.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heewon.cloud.file.domain.FileInfo;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

	FileInfo findFileInfoByNameAndUserInfo(String name, String userName);

}
