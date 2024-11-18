package com.heewon.cloud.link.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heewon.cloud.link.domain.LinkInfo;

@Repository
public interface LinkInfoRepository extends JpaRepository<LinkInfo, Long> {
	LinkInfo findLinkInfoById(String id);

	void deleteLinkInfoByCreateTimeBefore(LocalDateTime createTime);
}
