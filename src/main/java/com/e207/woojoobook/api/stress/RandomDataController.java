package com.e207.woojoobook.api.stress;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.domain.userbook.RegisterType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("stress")
@RequestMapping("/random")
@RestController
public class RandomDataController {

	private final RandomQueryService randomQueryService;

	@GetMapping("/book")
	public ResponseEntity<BookItem> findRandomBook() {
		return ResponseEntity.ok(randomQueryService.findBook());
	}

	@GetMapping("/tradable")
	public ResponseEntity<Long> findTradableUserBook(@RequestParam RegisterType registerType) {
		return ResponseEntity.ok(
			randomQueryService.findTradeAvailableUserbook(registerType.getDefaultTradeStatus()).getId());
	}

	@GetMapping("/users/borrowed")
	public ResponseEntity<Long> findBorrowedUserBook() {
		return ResponseEntity.ok(randomQueryService.findBorrowedUserbook().getId());
	}

	@GetMapping("/users/rented")
	public ResponseEntity<Long> findRentedUserBook() {
		return ResponseEntity.ok(randomQueryService.findRentedUserbook().getId());
	}

	@GetMapping("/users/exchangeable")
	public ResponseEntity<Long> findMyExchangeableUserBook() {
		return ResponseEntity.ok(randomQueryService.findMyExchangeableUserbook().getId());
	}

	@GetMapping("/users/offer/rental")
	public ResponseEntity<Long> findReceivedRentalOffer() {
		return ResponseEntity.ok(randomQueryService.findReceivedRentalOffer().getId());
	}

	@GetMapping("/users/offer/exchange")
	public ResponseEntity<Long> findReceivedExchangeOffer() {
		return ResponseEntity.ok(randomQueryService.findReceivedExchangeOffer().getId());
	}

	@GetMapping("/users/offer/extension")
	public ResponseEntity<Long> findReceivedExtensionOffer() {
		return ResponseEntity.ok(randomQueryService.findReceivedExtensionOffer().getId());
	}
}
