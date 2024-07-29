package com.e207.woojoobook.domain.userbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.user.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserbookReader {
	private final UserbookRepository userbookRepository;

	public Page<Userbook> findUserbookListByPage(User user, UserbookFindCondition condition, Pageable pageable) {
		if (condition.areaCodeList().isEmpty()) {
			condition.areaCodeList().add(user.getAreaCode());
		}

		return userbookRepository.findUserbookListByPage(condition, pageable);
	}

	public Userbook findOwnedUserbook(User user, Long userbookId) {
		Userbook userbook = userbookRepository.findWithUserById(userbookId)
			.orElseThrow(() -> new RuntimeException("사용자 도서가 존재하지 않을 때 던지는 예외"));
		if (!userbook.getUser().equals(user)) {
			throw new RuntimeException("도서에 대한 권한이 없을 때 던지는 예외");
		}
		if (!userbook.isPossibleToChangeRegisterType()) {
			throw new RuntimeException("도서의 등록 타입을 수정할 수 없을 때 던지는 예외");
		}

		return userbook;
	}

	public Userbook findDomain(Long id) {
		return userbookRepository.findFetchById(id).orElseThrow(() -> new RuntimeException("userbook not found"));
	}

	public Userbook createUserbook(User user, Book book, RegisterType registerType, QualityStatus quality) {
		Userbook userbook = Userbook.builder()
			.book(book)
			.user(user)
			.qualityStatus(quality)
			.registerType(registerType)
			.tradeStatus(registerType.getDefaultTradeStatus())
			.build();
		return userbookRepository.save(userbook);
	}

	public Userbook findUserbook(Long id) {
		return userbookRepository.findUserbookById(id).orElseThrow(() -> new RuntimeException("사용자 도서가 없습니다."));
	}
}
