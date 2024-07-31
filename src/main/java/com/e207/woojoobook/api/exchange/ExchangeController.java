package com.e207.woojoobook.api.exchange;

import static org.springframework.http.HttpStatus.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ExchangeController {

	private final ExchangeService exchangeService;

	@PostMapping("/userbooks/{receiverBookId}/exchanges/offer/{senderBookId}")
	public ResponseEntity<ExchangeResponse> create(@Valid @RequestBody ExchangeCreateRequest request) {
		ExchangeResponse response = exchangeService.create(request);
		return ResponseEntity.status(CREATED).body(response);
	}

	@PostMapping("/exchanges/offer/{id}")
	public ResponseEntity<Void> offerRespond(@PathVariable("id") Long id,
		@RequestBody ExchangeOfferRespondRequest request) {
		exchangeService.offerRespond(id, request);
		return ResponseEntity.status(OK).build();
	}

	@GetMapping("/exchanges")
	public ResponseEntity<Page<ExchangeResponse>> findExchangeOffer(@RequestBody ExchangeFindCondition condition
		, Pageable pageable) {
		Page<ExchangeResponse> response = exchangeService.findByCondition(condition, pageable);
		return ResponseEntity.status(OK).body(response);
	}

	@DeleteMapping("/exchanges/offer/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		exchangeService.delete(id);
		return ResponseEntity.status(NO_CONTENT).build();
	}
}
