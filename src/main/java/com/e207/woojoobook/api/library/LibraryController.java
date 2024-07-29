package com.e207.woojoobook.api.library;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.library.request.LibraryCreateRequest;
import com.e207.woojoobook.api.library.request.LibraryUpdateRequest;
import com.e207.woojoobook.api.library.response.LibraryListResponse;
import com.e207.woojoobook.api.library.response.LibraryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users/{userId}/libraries")
@RestController
public class LibraryController {
	private final LibraryService libraryService;

	@GetMapping
	public ResponseEntity<LibraryListResponse> findList(@PathVariable Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(libraryService.findList(userId));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<LibraryResponse> findById(@PathVariable Long userId, @PathVariable Long categoryId) {
		return ResponseEntity.status(HttpStatus.OK).body(libraryService.find(userId, categoryId));
	}

	@PostMapping("/categories")
	public ResponseEntity<LibraryResponse> create(@PathVariable Long userId,
		@Valid @RequestBody LibraryCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.create(userId, request));
	}

	@PutMapping("/categories/{categoryId}")
	public ResponseEntity<LibraryResponse> update(@PathVariable Long userId, @PathVariable Long categoryId,
		@RequestBody LibraryUpdateRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(libraryService.update(userId, categoryId, request));
	}

	@PutMapping("/categories/{from}/{to}")
	public ResponseEntity<?> swapOrderNumber(@PathVariable Long userId, @PathVariable Long from,
		@PathVariable Long to) {
		libraryService.swapOrderNumber(userId, from, to);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long categoryId) {
		libraryService.delete(userId, categoryId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
