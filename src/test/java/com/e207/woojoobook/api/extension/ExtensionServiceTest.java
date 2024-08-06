package com.e207.woojoobook.api.extension;

import static com.e207.woojoobook.domain.extension.ExtensionStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.extension.ExtensionRepository;
import com.e207.woojoobook.domain.rental.Rental;
import com.e207.woojoobook.domain.rental.RentalRepository;
import com.e207.woojoobook.domain.rental.RentalStatus;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.RegisterType;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;
import com.e207.woojoobook.domain.userbook.UserbookRepository;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ExtensionServiceTest {

	@Autowired
	private UserbookRepository userbookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RentalRepository rentalRepository;
	@Autowired
	private ExtensionService extensionService;
	@Autowired
	private ExtensionRepository extensionRepository;
	@MockBean
	private JavaMailSender mailSender;
	@MockBean
	private UserHelper userHelper;

	private Userbook userbook;
	private User user;
	private User owner;

	@BeforeEach
	void setUp() {
		// 대여 신청자
		user = User.builder()
			.email("trewq231@naver.com")
			.nickname("nickname")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.user = this.userRepository.save(user);

		// 도서 소유자
		User owner = User.builder()
			.email("kwondh5217@gmail.com")
			.nickname("nickname")
			.password("password")
			.areaCode("areaCode")
			.build();
		this.owner = this.userRepository.save(owner);

		// 사용자 도서
		Userbook build = Userbook.builder()
			.registerType(RegisterType.RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.user(owner)
			.build();
		this.userbook = this.userbookRepository.save(build);
	}

	@DisplayName("도서 소유자가 연장신청에 대해 수락할 경우 반납일이 7일이 늘어난다")
		// @Test
	void extensionRespond() {
		// given
		Rental save = createRental();
		Extension extension = createExtension(save);
		LocalDateTime currentEndDate = save.getEndDate();
		given(this.userHelper.findCurrentUser()).willReturn(owner);
		doNothing().when(mailSender).send(any(MimeMessage.class));

		// when
		this.extensionService.respond(extension.getId(), new ExtensionRespondRequest(true));

		// then
		Optional<Extension> byId = this.extensionRepository.findById(extension.getId());
		assertTrue(byId.isPresent());
		LocalDateTime endDate = byId.get().getRental().getEndDate();
		long days = ChronoUnit.DAYS.between(currentEndDate, endDate);
		assertEquals(byId.get().getExtensionStatus(), APPROVED);
		assertEquals(days, 7);
	}

	@DisplayName("연장 신청자가 연장신청을 취소한다")
	@Test
	void deleteExtension() {
		// given
		Rental save = createRental();
		Extension extension = createExtension(save);
		given(this.userHelper.findCurrentUser()).willReturn(user);
		doNothing().when(mailSender).send(any(MimeMessage.class));

		// when
		this.extensionService.delete(extension.getId());

		// then
		Optional<Extension> byId = this.extensionRepository.findById(extension.getId());
		assertFalse(byId.isPresent());
	}

	@DisplayName("연장은 중복으로 신청할 수 없다")
	@Test
	void rejectExtensionDuplicated() {
		// given
		Rental save = createRental();
		Extension extension = createExtension(save);
		given(this.userHelper.findCurrentUser()).willReturn(user);

		// expected
		ErrorException errorException = assertThrows(ErrorException.class,
			() -> this.extensionService.extensionRental(save.getId()));
		assertEquals(errorException.getErrorCode().getMessage(), ErrorCode.NotAcceptDuplicate.getMessage());
	}

	private Extension createExtension(Rental save) {
		Extension build = Extension.builder()
			.rental(save)
			.createdAt(LocalDateTime.now())
			.extensionStatus(OFFERING)
			.build();
		Extension extension = this.extensionRepository.save(build);
		return extension;
	}

	private Rental createRental() {
		Rental rental = Rental.builder()
			.user(user)
			.userbook(userbook)
			.rentalStatus(RentalStatus.IN_PROGRESS)
			.build();
		rental.respond(true);
		Rental save = this.rentalRepository.save(rental);
		return save;
	}
}