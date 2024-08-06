package com.e207.woojoobook.api.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.userbook.TradeStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class MyUserbookController {

	private final UserbookService userbookService;

	@GetMapping("/userbooks/likes")
	public ResponseEntity<Page<UserbookResponse>> findLikedUserbookListByPage(Pageable pageable) {
		return ResponseEntity.ok(userbookService.findLikedUserbookPageList(pageable));
	}

	@GetMapping("/userbooks/registered")
	public ResponseEntity<Page<UserbookResponse>> findRegisteredUserbookListByPage(
		@RequestParam(required = false) TradeStatus tradeStatus, Pageable pageable) {
		return ResponseEntity.ok(userbookService.findMyUserbookPage(tradeStatus, pageable));
	}

	@GetMapping("/userbooks/exchangable")
	public ResponseEntity<Page<UserbookResponse>> findMyExchangableUserbookListByPage(Pageable pageable) {
		return ResponseEntity.ok(userbookService.findMyExchangableUserbookPage(pageable));
	}
}
