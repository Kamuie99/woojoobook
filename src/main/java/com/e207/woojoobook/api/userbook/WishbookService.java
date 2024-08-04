package com.e207.woojoobook.api.userbook;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.userbook.response.WishbookResponse;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookReader;
import com.e207.woojoobook.domain.userbook.Wishbook;
import com.e207.woojoobook.domain.userbook.WishbookRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WishbookService {

	private final WishbookRepository wishbookRepository;
	private final UserbookReader userbookReader;
	private final UserHelper userHelper;

	@Transactional
	public WishbookResponse updateWishbook(Long userbookId, boolean wished) {
		User user = userHelper.findCurrentUser();
		Optional<Wishbook> wishbookOptional = wishbookRepository
			.findWithUserbookByUserAndUserbookId(user, userbookId);

		if (wished) {
			Wishbook wishbook = wishbookOptional.orElseThrow(
				() -> new ErrorException(ErrorCode.NotFound));
			deleteWishbook(wishbook);
			return new WishbookResponse(userbookId, false);
		} else {
			Userbook userbook = userbookReader.findUserbook(userbookId);
			wishbookOptional.ifPresentOrElse(
				wishBook -> {
					throw new ErrorException(ErrorCode.NotAcceptDuplicate);
				},
				() -> createWishbook(user, userbook)
			);
			return new WishbookResponse(userbookId, true);
		}
	}

	private void createWishbook(User user, Userbook userbook) {
		Wishbook wishbook = Wishbook.builder()
			.user(user)
			.userbook(userbook)
			.build();
		wishbookRepository.save(wishbook);
	}

	private void deleteWishbook(Wishbook wishbook) {
		wishbookRepository.delete(wishbook);
	}
}
