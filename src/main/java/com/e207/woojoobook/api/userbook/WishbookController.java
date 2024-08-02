package com.e207.woojoobook.api.userbook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.request.WishbookRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class WishbookController {

	private final WishbookService wishbookService;

	@PostMapping("/userbooks/{userbookId}/wish")
	public ResponseEntity<?> updateWishbook(@PathVariable(value = "userbookId") Long userbookId,
		@RequestBody WishbookRequest request) {
		wishbookService.updateWishbook(userbookId, request.wished());
		return ResponseEntity.ok().build();
	}
}
