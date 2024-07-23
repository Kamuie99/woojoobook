package com.e207.woojoobook.api.user.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.mail.Mail;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserBookEventListener {

	@Value("${spring.mail.username}")
	private String from;
	private final JavaMailSender mailSender;
	private final UserbookRepository userbookRepository;

	@EventListener
	public void handleChangeUserBookStatus(UserBookTradeStatusEvent event) {
		Userbook withWishBookById = this.userbookRepository.findWithWishBookById(event.userbook().getId());
		withWishBookById.getWishBooks().forEach(wishBook -> {
			String to = wishBook.getUser().getEmail();
			String title = "우주도서 관심도서 " + withWishBookById.getId() + "의 상태  변경 알림";
			String text = "관심있는 도서의 상태가 " + event.tradeStatus().getDescription() + " 으로 변경되었습니다.";
			sendMail(to, title, text);
		});
	}

	@Async
	protected void sendMail(String to, String title, String text) {
		mailSender.send(Mail.of(from, to, title, text));
	}
}
