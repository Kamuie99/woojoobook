package com.e207.woojoobook.api.rental;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RentalController {

	private final RentalService rentalService;

	@PostMapping("/userbooks/{userbooksId}/rentals/offer")
	public ResponseEntity<?> createRentalOffer(@PathVariable("userbooksId") Long userbooksId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.rentalService.rentalOffer(userbooksId));
	}

	@PutMapping("/rentals/offer/{offerId}")
	public ResponseEntity<?> offerRespond(@PathVariable("offerId") Long offerId,
		@RequestBody RentalOfferRespondRequest request) {
		this.rentalService.offerRespond(offerId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/rentals/offer/{offerId}")
	public ResponseEntity<?> delelteRentalOffer(@PathVariable("offerId") Long offerId) {
		this.rentalService.deleteRentalOffer(offerId);
		return ResponseEntity.ok().build();
	}
}
