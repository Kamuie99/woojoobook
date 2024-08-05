package com.e207.woojoobook.api.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.userbook.RegisterType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class MyUserbookController {

	private final UserbookService userbookService;

	@GetMapping("/userbooks/likes")
	public Page<UserbookResponse> findLikedUserbookListByPage(Pageable pageable) {
		return userbookService.findLikedUserbookPageList(pageable);
	}

	@GetMapping("/userbooks/registered")
	public Page<UserbookResponse> findRegisteredUserbookListByPage(
		@RequestParam(value = "registerType", required = false) RegisterType registerType, Pageable pageable) {
		return userbookService.findOwnedUserbookPage(registerType, pageable);
	}
}
