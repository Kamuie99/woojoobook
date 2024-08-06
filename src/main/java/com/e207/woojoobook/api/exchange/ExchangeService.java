package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.exchange.ExchangeStatus.*;
import static com.e207.woojoobook.domain.userbook.TradeStatus.*;

import java.util.Optional;

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
import com.e207.woojoobook.domain.exchange.TradeUserCondition;
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
		validatePossibleOffer(senderBook, receiverBook);
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
		TradeUserCondition userCondition = condition.userCondition();
		ExchangeStatus exchangeStatus = condition.exchangeStatus();
		return exchangeRepository.findByStatusAndUserCondition(userId, exchangeStatus, userCondition, pageable)
			.map(ExchangeResponse::of);
	}

	@Transactional
	public void respondOffer(Long id, ExchangeOfferRespondRequest request) {
		Exchange exchange = findDomain(id);
		validateBookOwner(userHelper.findCurrentUser(), exchange.getReceiverBook());
		exchange.respond(request.status());
		updateUserbookStatus(exchange);
		eventPublisher.publishEvent(new ExchangeRespondEvent(exchange));
	}

	public void delete(Long id) {
		Exchange exchange = findExchange(id);
		User sessionUser = userHelper.findCurrentUser();
		validateExchangeSender(sessionUser, exchange);
		exchangeRepository.deleteById(id);
	}

	private void validatePossibleOffer(Userbook senderBook, Userbook receiverBook) {
		validateDuplicatedExchange(senderBook, receiverBook);
		validateRegisterType(senderBook, receiverBook);
		validateUserbooks(senderBook, receiverBook);
	}

	private void validateDuplicatedExchange(Userbook senderBook, Userbook receiverBook) {
		Optional<Exchange> result = exchangeRepository.findBySenderBookAndReceiverBook(senderBook, receiverBook);
		if (result.isPresent()) {
			throw new ErrorException(ErrorCode.NotAcceptDuplicate);
		}
	}

	private void validateRegisterType(Userbook senderBook, Userbook receiverBook) {
		if (!senderBook.getRegisterType().canExchange() || !receiverBook.getRegisterType().canExchange()) {
			throw new ErrorException(
				ErrorCode.InvalidAccess,
				"교환 신청이 불가능한 도서입니다. registerType "
					+ "= senderBook: " + senderBook.getRegisterType()
					+ ", receiverBook: " + receiverBook.getRegisterType()
			);
		}
	}

	private void validateUserbooks(Userbook senderBook, Userbook receiverBook) {
		User sessionUser = userHelper.findCurrentUser();
		boolean isValidSenderBook = sessionUser.getId().equals(senderBook.getUser().getId());
		boolean isValidReceiverBook = !sessionUser.getId().equals(receiverBook.getUser().getId());
		if (!isValidSenderBook || !isValidReceiverBook) {
			throw new ErrorException(
				ErrorCode.InvalidAccess,
				"교환하려는 도서가 잘못 선택되었습니다. match result "
					+ "= senderBook: " + isValidSenderBook
					+ ", receiverBook: " + isValidReceiverBook
			);
		}
	}

	private void validateBookOwner(User user, Userbook userbook) {
		User pair = userbook.getUser();
		if (!user.getId().equals(pair.getId()))
			throw new ErrorException(ErrorCode.ForbiddenError);
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
			throw new RuntimeException(
				"도서 상태로 인해 교환이 불가능합니다. tradeStatus "
					+ "= senderBook: " + senderBook.getTradeStatus()
					+ ", receiverBook: " + receiverBook.getTradeStatus());
		}
	}

	private void validateExchangeSender(User user, Exchange exchange) {
		if (!user.getId().equals(exchange.getSender().getId()))
			throw new ErrorException(ErrorCode.ForbiddenError);
	}

	private Exchange findExchange(Long id) {
		return exchangeRepository.findById(id)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
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
