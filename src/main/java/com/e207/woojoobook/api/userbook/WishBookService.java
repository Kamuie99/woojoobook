package com.e207.woojoobook.api.userbook;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.userbook.response.WishBookResponse;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookReader;
import com.e207.woojoobook.domain.userbook.WishBook;
import com.e207.woojoobook.domain.userbook.WishBookRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WishBookService {

	private final WishBookRepository wishBookRepository;
	private final UserbookReader userbookReader;
	private final UserHelper userHelper;

	@Transactional
	public WishBookResponse updateWishBook(Long userbookId, boolean wished) {
		User user = userHelper.findCurrentUser();
		Optional<WishBook> wishBookOptional = wishBookRepository
			.findWithUserbookByUserIdAndUserbookId(user.getId(), userbookId);

		if (wished) {
			WishBook wishBook = wishBookOptional.orElseThrow(
				() -> new ErrorException(ErrorCode.NotFound));
			deleteWishBook(wishBook);
			return new WishBookResponse(userbookId, false);
		} else {
			Userbook userbook = userbookReader.findUserbook(userbookId);
			wishBookOptional.ifPresentOrElse(
				wishBook -> { throw new ErrorException(ErrorCode.NotAcceptDuplicate); },
				() -> createWishBook(user, userbook)
			);
			return new WishBookResponse(userbookId, true);
		}
	}

	private void createWishBook(User user, Userbook userbook) {
		WishBook wishBook = WishBook.builder()
			.user(user)
			.userbook(userbook)
			.build();
		wishBookRepository.save(wishBook);
	}

	private void deleteWishBook(WishBook wishBook) {
		Userbook userbook = wishBook.getUserbook();
		wishBookRepository.delete(wishBook);
		userbook.getWishBooks().remove(wishBook);
	}
}
