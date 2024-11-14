package com.heewon.cloud.link.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heewon.cloud.link.service.LinkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkController {
	private final LinkService linkService;

}
