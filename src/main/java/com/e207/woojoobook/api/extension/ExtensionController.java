package com.e207.woojoobook.api.extension;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.extension.request.ExtensionFindCondition;
import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ExtensionController {

	private final ExtensionService extensionService;

	@PostMapping("/rentals/{rentalId}/extensions")
	public ResponseEntity<ExtensionOfferResponse> extension(@PathVariable("rentalId") Long rentalId) {
		Long extensionId = this.extensionService.extensionRental(rentalId);
		return ResponseEntity.ok(new ExtensionOfferResponse(extensionId));
	}

	@GetMapping("/extensions")
	public ResponseEntity<Page<ExtensionResponse>> findPage(@ModelAttribute ExtensionFindCondition condition,
		@PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
		Page<ExtensionResponse> response = extensionService.findByCondition(condition, pageable);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/extensions/{extensionId}")
	public ResponseEntity<?> extensionRespond(@PathVariable("extensionId") Long extensionId,
		@RequestBody ExtensionRespondRequest request) {
		this.extensionService.respond(extensionId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/extensions/{extensionId}")
	public ResponseEntity<?> extensionDelete(@PathVariable("extensionId") Long extensionId) {
		this.extensionService.delete(extensionId);
		return ResponseEntity.ok().build();
	}
}
