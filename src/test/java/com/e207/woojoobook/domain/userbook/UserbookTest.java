package com.e207.woojoobook.domain.userbook;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.e207.woojoobook.domain.book.Book;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;

class UserbookTest {

	@DisplayName("회원도서의 상태가 접근불가한 상태가 아니라면 대여를 생성한다")
	@ParameterizedTest
	@MethodSource
	void createRental(RegisterType registerType, TradeStatus tradeStatus) {
		// given
		Book book = createBook();
		User owner = createUser(1L);
		User customer = createUser(2L);

		Userbook userbook = createUserbook(book, owner, registerType, tradeStatus);

		// when
		Rental rental = userbook.createRental(customer);

		// then
		assertAll(
			() -> assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.OFFERING),
			() -> assertThat(rental.getUser().getId()).isEqualTo(customer.getId()),
			() -> assertThat(rental.getStartDate()).isNull(),
			() -> assertThat(rental.getEndDate()).isNull()
		);
	}


	@DisplayName("회원도서의 상태가 접근불가한 상태라면 에러를 발생한다")
	@ParameterizedTest
	@MethodSource
	void createRental_fail(RegisterType registerType, TradeStatus tradeStatus) {
		// given
		String errorMessage = "접근할 수 없는 상태입니다";
		Book book = createBook();
		User owner = createUser(1L);
		User customer = createUser(2L);

		Userbook userbook = createUserbook(book, owner, registerType, tradeStatus);

		// expected
		IllegalStateException exception = assertThrows(IllegalStateException.class,
			() -> userbook.createRental(customer));
		assertThat(exception.getMessage()).isEqualTo(errorMessage);
	}

	private static Book createBook() {
		return Book.builder()
			.build();
	}

	private static Stream<Arguments> createRental() {
		return Stream.of(
			Arguments.of(RegisterType.RENTAL, RegisterType.RENTAL.getDefaultTradeStatus()),
			Arguments.of(RegisterType.EXCHANGE, RegisterType.EXCHANGE.getDefaultTradeStatus()),
			Arguments.of(RegisterType.RENTAL_EXCHANGE, RegisterType.RENTAL_EXCHANGE.getDefaultTradeStatus())
		);
	}

	private static Stream<Arguments> createRental_fail() {
		return Stream.of(
			Arguments.of(RegisterType.RENTAL, TradeStatus.RENTED),
			Arguments.of(RegisterType.EXCHANGE, TradeStatus.EXCHANGED),
			Arguments.of(RegisterType.RENTAL_EXCHANGE, TradeStatus.UNAVAILABLE),
			Arguments.arguments(RegisterType.INACTIVE, TradeStatus.RENTAL_AVAILABLE)
		);
	}

	private static Userbook createUserbook(Book book, User owner, RegisterType registerType, TradeStatus tradeStatus) {
		return Userbook.builder()
			.book(book)
			.user(owner)
			.registerType(registerType)
			.tradeStatus(tradeStatus)
			.build();
	}

	private static User createUser(long id) {
		return User.builder()
			.id(id)
			.build();
	}

}