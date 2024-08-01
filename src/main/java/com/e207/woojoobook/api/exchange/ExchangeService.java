package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.exchange.event.ExchangeRespondEvent;
import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeFindCondition;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.exchange.ExchangeStatus;
import com.e207.woojoobook.domain.exchange.ExchangeUserCondition;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookReader;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExchangeService {

	private final ExchangeRepository exchangeRepository;
	private final UserbookReader userbookReader;
	private final ApplicationEventPublisher eventPublisher;
	private final UserHelper userHelper;

	@Transactional
	public ExchangeResponse create(ExchangeCreateRequest request) {
		Userbook senderBook = userbookReader.findDomain(request.senderBookId());
		Userbook receiverBook = userbookReader.findDomain(request.receiverBookId());
		Exchange exchange = createExchange(senderBook, receiverBook);
		Exchange createdExchange = exchangeRepository.save(exchange);
		return ExchangeResponse.of(createdExchange);
	}

	@Transactional(readOnly = true)
	public ExchangeResponse findById(Long id) {
		Exchange exchange = findDomain(id);
		Long senderBookId = exchange.getSenderBook().getId();
		Long receiverBookId = exchange.getReceiverBook().getId();
		userbookReader.findDomain(senderBookId);
		userbookReader.findDomain(receiverBookId);
		return ExchangeResponse.of(exchange);
	}

	@Transactional(readOnly = true)
	public Exchange findDomain(Long id) {
		return exchangeRepository.findByIdWithUserbookAndUser(id)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	@Transactional(readOnly = true)
	public Page<ExchangeResponse> findByCondition(ExchangeFindCondition condition, Pageable pageable) {
		Long userId = userHelper.findCurrentUser().getId();
		ExchangeUserCondition userCondition = condition.userCondition();
		ExchangeStatus exchangeStatus = condition.exchangeStatus();
		return exchangeRepository.findByStatusAndUserCondition(userId, exchangeStatus, userCondition, pageable)
			.map(ExchangeResponse::of);
	}

	@Transactional
	public void offerRespond(Long id, ExchangeOfferRespondRequest request) {
		Exchange exchange = findDomain(id);
		validateBookOwner(exchange);
		exchange.respond(request.status());
		updateUserbookStatus(exchange);
		eventPublisher.publishEvent(new ExchangeRespondEvent(exchange));
	}

	public void delete(Long id) {
		validateExchangeSender(id);
		exchangeRepository.deleteById(id);
	}

	private void updateUserbookStatus(Exchange exchange) {
		if (APPROVED.equals(exchange.getExchangeStatus())) {
			Userbook senderBook = exchange.getSenderBook();
			Userbook receiverBook = exchange.getReceiverBook();
			validateExchangeable(senderBook, receiverBook);
			senderBook.updateTradeStatus(EXCHANGED);
			receiverBook.updateTradeStatus(EXCHANGED);
		}
	}

	private void validateExchangeable(Userbook senderBook, Userbook receiverBook) {
		if (!senderBook.isAvailable() || !receiverBook.isAvailable()) {
			throw new RuntimeException( // TODO <jhl221123> 대여 상태 검증 필요
				"대여가 불가능한 도서 상태입니다. status "
					+ "= 신청자 도서: " + senderBook.isAvailable()
					+ ", 수신자 도서: " + receiverBook.isAvailable());
		}
	}

	private void validateBookOwner(Exchange exchange) {
		User receiver = exchange.getReceiver();
		User sessionUser = userHelper.findCurrentUser();
		if (!sessionUser.getId().equals(receiver.getId()))
			throw new ErrorException(ErrorCode.ForbiddenError);
	}

	private void validateExchangeSender(Long id) {
		Exchange exchange = exchangeRepository.findById(id)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
		User sessionUser = userHelper.findCurrentUser();
		if (!sessionUser.getId().equals(exchange.getSender().getId()))
			throw new ErrorException(ErrorCode.ForbiddenError);
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.sender(senderBook.getUser())
			.receiver(receiverBook.getUser())
			.senderBook(senderBook)
			.receiverBook(receiverBook)
			.build();
	}
}
