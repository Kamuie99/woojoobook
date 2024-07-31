package com.e207.woojoobook.api.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;
import com.e207.woojoobook.api.userbook.request.UserbookUpdateRequest;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.global.security.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/userbooks")
@RestController
public class UserbookController {

	private final UserbookService userbookService;

	@GetMapping
	public ResponseEntity<Page<UserbookResponse>> findUserbookPageList(
		@ModelAttribute UserbookPageFindRequest condition, @PageableDefault(sort = "title") Pageable pageable) {
		Page<UserbookResponse> userbookPageList = userbookService.findUserbookPageList(condition, pageable);
		return ResponseEntity.ok(userbookPageList);
	}

	@PostMapping
	public ResponseEntity<UserbookResponse> createUserbook(@Valid @RequestBody UserbookCreateRequest userbookCreateRequest) {
		// todo 예외 처리: Validation 예외 처리
		return ResponseEntity.ok(userbookService.createUserbook(userbookCreateRequest));
	}

	@PutMapping("/{userbookId}")
	public ResponseEntity<UserbookResponse> updateUserbook(@Valid @RequestBody UserbookUpdateRequest userbookUpdateRequest,
		@PathVariable Long userbookId) {
		// todo 예외 처리: Validation 예외 처리
		return ResponseEntity.ok(userbookService.updateUserbook(userbookId, userbookUpdateRequest));
	}
}