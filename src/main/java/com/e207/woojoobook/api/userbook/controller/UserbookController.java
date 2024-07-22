package com.e207.woojoobook.api.userbook.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.service.UserbookService;
import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserbookController {

	private final UserbookService userbookService;

	@GetMapping("/userbooks")
	public ResponseEntity<?> findUserbookPageList(UserbookPageFindRequest condition,
		@PageableDefault(sort = "title") Pageable pageable) {
		var userbookPageListResult = userbookService.findUserbookPageList(condition, pageable);

		return ResponseEntity.ok(userbookPageListResult);
	}
}
