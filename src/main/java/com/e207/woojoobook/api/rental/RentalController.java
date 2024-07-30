package com.e207.woojoobook.api.rental;

import static org.springframework.http.HttpStatus.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.rental.request.RentalFindCondition;
import com.e207.woojoobook.api.rental.request.RentalOfferRespondRequest;
import com.e207.woojoobook.api.rental.response.RentalResponse;

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

	@GetMapping("/rentals/offer")
	public ResponseEntity<Page<RentalResponse>> findExchangeOffer(@RequestBody RentalFindCondition condition
		, Pageable pageable) {
		Page<RentalResponse> response = rentalService.findRentalOffer(condition, pageable);
		return ResponseEntity.status(OK).body(response);
	}

	@DeleteMapping("/rentals/offer/{offerId}")
	public ResponseEntity<?> delelteRentalOffer(@PathVariable("offerId") Long offerId) {
		this.rentalService.deleteRentalOffer(offerId);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/rentals/{rentalId}/return")
	public ResponseEntity<?> giveBack(@PathVariable("rentalId") Long rentalId) {
		this.rentalService.giveBack(rentalId);
		return ResponseEntity.ok().build();
	}
}
