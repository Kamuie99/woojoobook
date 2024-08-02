package com.e207.woojoobook.api.userbook.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.api.verification.MailSender;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.Wishbook;
import com.e207.woojoobook.domain.userbook.WishbookRepository;
import com.e207.woojoobook.domain.userbook.event.UserBookTradeStatusUpdateEvent;
import com.e207.woojoobook.global.mail.Mail;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserbookMailSendEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final MailSender mailSender;
	private final WishbookRepository wishBookRepository;

	@EventListener
	public void handleChangeUserBookTradeStatus(UserBookTradeStatusUpdateEvent event) {
		Userbook userbook = event.userbook();
		List<Wishbook> wishbooks = wishBookRepository.findWithUserByUser(userbook.getUser());
		wishbooks.forEach(wishBook -> {
			String to = wishBook.getUser().getEmail();
			String title = "우주도서 관심도서 " + event.userbook().getBook().getTitle() + "의 상태  변경 알림";
			String text = "관심있는 도서의 상태가 " + event.tradeStatus().getDescription() + " 으로 변경되었습니다.";
			sendMail(to, title, text);
		});
	}

	@Async
	protected void sendMail(String to, String title, String text) {
		mailSender.send(Mail.of(from, to, title, text));
	}
}
