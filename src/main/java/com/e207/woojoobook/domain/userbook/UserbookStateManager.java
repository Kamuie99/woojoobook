package com.e207.woojoobook.domain.userbook;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.api.exchange.event.ExchangeRespondEvent;
import com.e207.woojoobook.api.rental.event.RentalOfferEvent;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;

import lombok.RequiredArgsConstructor;

// TODO <fosong98> api 패키지에 대한 의존성 제거
@RequiredArgsConstructor
@Component
public class UserbookStateManager {

	private final ApplicationEventPublisher eventPublisher;
	private final RentalRepository rentalRepository;
	private final ExchangeRepository exchangeRepository;

	public void updateRegisterType(Userbook userbook, RegisterType registerType) {
		RegisterType prevRegisterType = userbook.getRegisterType();
		TradeStatus prevTradeStatus = userbook.getTradeStatus();

		userbook.updateRegisterType(registerType);

		if (prevRegisterType != userbook.getRegisterType()) {
			processRegisterTypeUpdated(userbook, prevRegisterType);
			eventPublisher.publishEvent(new UserBookTradeStatusUpdateEvent(userbook, prevTradeStatus));
		}
	}

	public void updateQualityStatus(Userbook userbook, QualityStatus qualityStatus) {
		userbook.updateQualityStatus(qualityStatus);
	}

	// TODO <fosong98> 대여나 교환에 대해 모두 거절하는 Util 빈 필요
	private void processRegisterTypeUpdated(Userbook userbook, RegisterType prev) {
		if (isRentalDisable(userbook, prev)) {
			rentalRepository.findAllByUserbook(userbook).stream()
				.filter(Rental::isOffering)
				.peek(rental -> rental.respond(false))
				.map(rental -> new RentalOfferEvent(rental, false))
				.forEach(eventPublisher::publishEvent);
		}

		if (isExchangeDisable(userbook, prev)) {
			exchangeRepository.findAllByReceiverBook(userbook)
				.stream()
				.filter(Exchange::isOffering)
				.peek(exchange -> exchange.respond(false))
				.map(exchange -> new ExchangeRespondEvent(exchange, false))
				.forEach(eventPublisher::publishEvent);
		}
	}

	private boolean isRentalDisable(Userbook userbook, RegisterType prev) {
		RegisterType current = userbook.getRegisterType();
		return prev.canRent() && !current.canRent();
	}

	private boolean isExchangeDisable(Userbook userbook, RegisterType prev) {
		RegisterType current = userbook.getRegisterType();
		return prev.canExchange() && !current.canExchange();
	}
}
