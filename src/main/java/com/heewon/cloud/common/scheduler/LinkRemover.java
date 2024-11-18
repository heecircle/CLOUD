package com.heewon.cloud.common.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.heewon.cloud.link.repository.LinkInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkRemover {
	private final LinkInfoRepository linkInfoRepository;

	@Scheduled(cron = "0 0 0 * * *")
	public void run() {
		linkInfoRepository.deleteLinkInfoByCreateTimeBefore(LocalDateTime.now().minusHours(3));
	}
}
