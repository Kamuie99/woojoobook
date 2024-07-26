package com.e207.woojoobook.api.library;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.library.request.LibraryCreateRequest;
import com.e207.woojoobook.api.library.request.LibraryUpdateRequest;
import com.e207.woojoobook.api.library.response.LibraryListResponse;
import com.e207.woojoobook.api.library.response.LibraryResponse;
import com.e207.woojoobook.domain.library.Library;
import com.e207.woojoobook.domain.library.LibraryRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LibraryService {

	private final LibraryRepository libraryRepository;
	private final UserHelper userHelper;

	private Library findByIdAndUserId(Long id, Long userId) {
		return libraryRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new RuntimeException("library not found"));
	}

	@Transactional(readOnly = true)
	public LibraryListResponse findList() {
		Long userId = userHelper.findCurrentUser().getId();
		List<Library> categoryList = libraryRepository.findByUserId(userId);

		List<LibraryResponse> libraryResponses = categoryList.stream()
			.map(LibraryResponse::of)
			.collect(Collectors.toList());

		return LibraryListResponse.builder()
			.libraryList(libraryResponses)
			.build();
	}

	@Transactional(readOnly = true)
	public LibraryResponse find(Long id) {
		Long userId = userHelper.findCurrentUser().getId();
		Library library = findByIdAndUserId(id, userId);
		return LibraryResponse.of(library);
	}

	@Transactional
	public LibraryResponse create(LibraryCreateRequest request) {
		User user = userHelper.findCurrentUser();
		Long newOrderNumber = findMaxOrderNumber() + 1L;
		Library library = libraryRepository.save(Library.builder()
			.user(user)
			.name(request.categoryName())
			.bookList(request.books())
			.orderNumber(newOrderNumber)
			.build());

		return LibraryResponse.of(library);
	}

	@Transactional
	public LibraryResponse update(Long id, LibraryUpdateRequest request) {
		Long userId = userHelper.findCurrentUser().getId();
		Library library = findByIdAndUserId(id, userId);
		library.update(request.categoryName(), request.books());
		return LibraryResponse.of(library);
	}

	@Transactional
	public void delete(Long id) {
		Long userId = userHelper.findCurrentUser().getId();
		Library library = findByIdAndUserId(id, userId);
		libraryRepository.delete(library);
	}

	@Transactional
	public void swapOrderNumber(Long fromId, Long toId) {
		Long userId = userHelper.findCurrentUser().getId();
		Library fromLibrary = findByIdAndUserId(fromId, userId);
		Library toLibrary = findByIdAndUserId(toId, userId);

		Long tempOrderNumber = fromLibrary.getOrderNumber();
		fromLibrary.updateOrderNumber(toLibrary.getOrderNumber());
		toLibrary.updateOrderNumber(tempOrderNumber);

		libraryRepository.save(fromLibrary);
		libraryRepository.save(toLibrary);
	}

	@Transactional(readOnly = true)
	public Long findMaxOrderNumber() {
		return libraryRepository.findMaxOrderNumber().orElse(0L);
	}
}
