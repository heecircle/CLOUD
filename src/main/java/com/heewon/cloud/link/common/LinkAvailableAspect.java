package com.heewon.cloud.link.common;

import java.security.cert.CertificateExpiredException;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.heewon.cloud.link.domain.LinkInfo;
import com.heewon.cloud.link.dto.LinkDto;
import com.heewon.cloud.link.repository.LinkInfoRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@RequiredArgsConstructor
@Component
public class LinkAvailableAspect {
	private final LinkInfoRepository linkInfoRepository;

	@Before("@annotation(com.heewon.cloud.link.common.LinkAvailable) && args(linkDto, ..)")
	public void linkCheck(JoinPoint joinPoint, LinkDto<?> linkDto) throws
		Throwable {

		System.out.println(";saslkdjf;alskdjf;alskjdf");
		LinkInfo info = linkInfoRepository.findLinkInfoById(linkDto.getLink());
		if (info == null) {
			throw new CertificateExpiredException("유효하지 않은 링크입니다.");
		}
		if (info.getCreateTime().plusHours(3).isBefore(LocalDateTime.now())) {
			throw new CertificateExpiredException("유효하지 않은 링크입니다.");
		}
		linkDto.setLinkInfo(info);

	}

}
