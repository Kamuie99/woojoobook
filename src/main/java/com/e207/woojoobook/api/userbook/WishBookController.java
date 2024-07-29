package com.e207.woojoobook.api.userbook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.request.WishBookRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class WishBookController {

	private final WishBookService wishBookService;

	@PostMapping("/userbooks/{userbookId}/wish")
	public ResponseEntity<?> updateWishBook(@PathVariable(value = "userbookId") Long userbookId,
		@RequestBody WishBookRequest request) {
		wishBookService.updateWishBook(userbookId, request.wished());
		return ResponseEntity.ok().build();
	}
}
