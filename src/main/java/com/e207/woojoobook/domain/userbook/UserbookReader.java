package com.e207.woojoobook.domain.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserbookReader {

	private final UserbookRepository userbookRepository;
	private final WishbookRepository wishbookRepository;

	public Page<Userbook> findPage(UserbookFindCondition condition, Pageable pageable) {
		return this.userbookRepository.findUserbookListByPage(condition, pageable);
	}

	public Page<Userbook> findLikedPageByUser(User user, Pageable pageable) {
		return this.wishbookRepository.findWishbookPageWithUserbookByUser(user, pageable)
			.map(Wishbook::getUserbook);
	}

	public Userbook findOwnedUserbook(User user, Long userbookId) {
		Userbook userbook = this.userbookRepository.findWithUserById(userbookId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		if (!userbook.getUser().equals(user)) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}
		if (!userbook.isPossibleToChangeRegisterType()) {
			throw new ErrorException(ErrorCode.InvalidAccess);
		}

		return userbook;
	}

	public Userbook findDomain(Long id) {
		return this.userbookRepository.findByIdWithUserAndBook(id)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	public Userbook createUserbook(User user, Book book, RegisterType registerType, QualityStatus quality) {
		Userbook userbook = Userbook.builder()
			.book(book)
			.user(user)
			.qualityStatus(quality)
			.registerType(registerType)
			.tradeStatus(registerType.getDefaultTradeStatus())
			.build();
		return this.userbookRepository.save(userbook);
	}

	public Userbook findUserbook(Long id) {
		return this.userbookRepository.findUserbookById(id).orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}
}
