package com.heewon.cloud.link.common;

import java.security.cert.CertificateExpiredException;
import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.heewon.cloud.link.domain.LinkInfo;
import com.heewon.cloud.link.repository.LinkInfoRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@RequiredArgsConstructor
@Component
public class LinkAvailableAspect {
	private final LinkInfoRepository linkInfoRepository;

	@Around("@annotation(com.heewon.cloud.link.common.LinkAvailable) && args(link,.. )")
	public Object Linkcheck(final ProceedingJoinPoint joinPoint, final String link) throws Throwable {
		LinkInfo info = linkInfoRepository.findLinkInfoById(link);
		if (info == null) {
			throw new CertificateExpiredException("유효하지 않은 링크입니다.");
		}
		if (info.getCreateTime().plusHours(3).isBefore(LocalDateTime.now())) {
			throw new CertificateExpiredException("유효하지 않은 링크입니다.");
		}
		return info;
	}

}
