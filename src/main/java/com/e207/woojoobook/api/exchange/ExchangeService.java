package com.e207.woojoobook.api.exchange;

import static com.e207.woojoobook.domain.userbook.TradeStatus.*;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.exchange.event.ExchangeRespondEvent;
import com.e207.woojoobook.api.exchange.request.ExchangeCreateRequest;
import com.e207.woojoobook.api.exchange.request.ExchangeOfferRespondRequest;
import com.e207.woojoobook.api.exchange.response.ExchangeResponse;
import com.e207.woojoobook.api.userbook.UserbookService;
import com.e207.woojoobook.domain.exchange.Exchange;
import com.e207.woojoobook.domain.exchange.ExchangeRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExchangeService {

	private final ExchangeRepository exchangeRepository;
	private final UserbookService userbookService;
	private final ApplicationEventPublisher eventPublisher;
	private final UserHelper userHelper;

	@Transactional
	public ExchangeResponse create(ExchangeCreateRequest request) {
		Userbook senderBook = userbookService.findDomain(request.senderBookId());
		Userbook receiverBook = userbookService.findDomain(request.receiverBookId());
		Exchange exchange = createExchange(senderBook, receiverBook);
		Exchange createdExchange = exchangeRepository.save(exchange);
		return ExchangeResponse.of(createdExchange);
	}

	@Transactional(readOnly = true)
	public ExchangeResponse findById(Long id) {
		Exchange exchange = findDomain(id);
		Long senderBookId = exchange.getSenderBook().getId();
		Long receiverBookId = exchange.getReceiverBook().getId();
		userbookService.findDomain(senderBookId);
		userbookService.findDomain(receiverBookId);
		return ExchangeResponse.of(exchange);
	}

	public Exchange findDomain(Long id) {
		return exchangeRepository.findFetchById(id).orElseThrow(() -> new RuntimeException("Exchange not found"));
	}

	@Transactional
	public void offerRespond(Long id, ExchangeOfferRespondRequest request) {
		Exchange exchange = findDomain(id);
		validateBookOwner(exchange);
		exchange.respond(request.isApproved());
		updateUserbookStatus(request, exchange);
		eventPublisher.publishEvent(new ExchangeRespondEvent(exchange, request.isApproved()));
	}

	public void delete(Long id) {
		validateExchangeSender(id);
		exchangeRepository.deleteById(id);
	}

	private void updateUserbookStatus(ExchangeOfferRespondRequest request, Exchange exchange) {
		if (request.isApproved()) {
			Userbook senderBook = exchange.getSenderBook();
			Userbook receiverBook = exchange.getReceiverBook();
			validateExchangeable(senderBook, receiverBook);
			exchange.registerExchangeDate(LocalDateTime.now());
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
		User receiver = exchange.getReceiverBook().getUser();
		User sessionUser = userHelper.findCurrentUser();
		if (sessionUser.getId() != receiver.getId())  // TODO <jhl221123> receiver.getId() 호출 시, N+1 쿼리
			throw new RuntimeException("You are not allowed to respond to this exchange.");
	}

	private void validateExchangeSender(Long id) {
		Exchange exchange = exchangeRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Exchange not found"));
		User sessionUser = userHelper.findCurrentUser();
		if (sessionUser.getId() != exchange.getSenderBook().getUser().getId())  // TODO <jhl221123> 교환 -> 사용자 참조 필요
			throw new RuntimeException("You are not allowed to delete to this exchange.");
	}

	private Exchange createExchange(Userbook senderBook, Userbook receiverBook) {
		return Exchange.builder()
			.senderBook(senderBook)
			.receiverBook(receiverBook)
			.build();
	}
}
