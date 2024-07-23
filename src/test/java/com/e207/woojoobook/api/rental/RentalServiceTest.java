package com.e207.woojoobook.api.rental;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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

import com.e207.woojoobook.api.extension.ExtensionService;
import com.e207.woojoobook.api.rental.response.RentalOfferResponse;
import com.e207.woojoobook.domain.book.WishBook;
import com.e207.woojoobook.domain.book.WishBookRepository;
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
import com.e207.woojoobook.global.helper.UserHelper;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RentalServiceTest {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private UserbookRepository userbookRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishBookRepository wishBookRepository;
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
        // 도서를 관심등록한 유저
        User user = User.builder()
                .email("kwondh5217@gmail.com")
                .nickname("nickname")
                .password("password")
                .areaCode("areaCode")
                .build();
        User wishBookUser = this.userRepository.save(user);

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
                .email("test@test.com")
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

        // 도서 관심
        WishBook wishBook = WishBook.builder()
                .user(wishBookUser)
                .userbook(userbook)
                .build();
        WishBook save = this.wishBookRepository.save(wishBook);
        userbook.getWishBooks().add(save);
        this.userbook = this.userbookRepository.save(userbook);
    }

    @DisplayName("도서의 ID에 대해 대여를 신청한다")
    @Test
    void rentalOffer() {
        // given
        Long userbooksId = userbook.getId();
        given(this.userHelper.findCurrentUser()).willReturn(user);

        // when
        RentalOfferResponse rentalOfferResponse = this.rentalService.rentalOffer(userbooksId);

        // then
        assertNotNull(rentalOfferResponse);

        Optional<Rental> byId = this.rentalRepository.findById(rentalOfferResponse.rentalId());
        assertTrue(byId.isPresent());

        Rental createdRental = byId.get();
        assertEquals(createdRental.getUserbook().getId(), userbooksId);
        assertEquals(createdRental.getUser().getId(), user.getId());
    }

    // TODO : 예외처리
    @DisplayName("존재하지 않는 도서에 대해서는 대여 신청을 할 수 없다")
    @Test
    void rentalOffer_doseNotExist_fail() {
        // given
        Long invalidUserbookId = 241234312L;
        String expectedMessage = "존재하지 않는 도서입니다.";

        // expected
        Exception exception = assertThrows(RuntimeException.class,
                () -> this.rentalService.rentalOffer(invalidUserbookId));
        assertEquals(exception.getMessage(), expectedMessage);
    }

    // TODO : 예외처리
    @DisplayName("대여 불가능한 도서에 대해서는 대여 신청을 할 수 없다")
    @Test
    void rentalOffer_tradeStatusUnavailable_fail() {
        // given
        userbook.inactivate();
        userbook = this.userbookRepository.save(userbook);
        String expectedMessage = "접근이 불가능한 도서 상태입니다.";

        // expected
        Exception exception = assertThrows(RuntimeException.class,
                () -> this.rentalService.rentalOffer(userbook.getId()));
        assertEquals(exception.getMessage(), expectedMessage);
    }

    @DisplayName("회원이 대여신청을 수락한다")
    @Test
    void offerRespond_approve() {
        // given
        Rental rental = Rental.builder()
                .user(user)
                .userbook(userbook)
                .build();
        Rental save = this.rentalRepository.save(rental);
        RentalOfferRespondRequest request = new RentalOfferRespondRequest(true);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        given(this.userHelper.findCurrentUser()).willReturn(owner);

        // when
        this.rentalService.offerRespond(save.getId(), request);

        // then
        Optional<Rental> byId = this.rentalRepository.findById(save.getId());
        assertTrue(byId.isPresent());

        Rental findById = byId.get();
        assertNotNull(findById.getStartDate());

        userbook = findById.getUserbook();
        assertEquals(userbook.getTradeStatus(), TradeStatus.RENTED);
    }

    @DisplayName("회원이 발생한 대여신청을 삭제한다")
    @Test
    void deleteRentalOffer() {
        // given
        Rental rental = Rental.builder()
                .user(user)
                .userbook(userbook)
                .build();
        Rental save = this.rentalRepository.save(rental);
        given(this.userHelper.findCurrentUser()).willReturn(user);

        // when
        this.rentalService.deleteRentalOffer(save.getId());

        // then
        Optional<Rental> byId = this.rentalRepository.findById(save.getId());
        assertFalse(byId.isPresent());
    }

    @DisplayName("도서 소유자가 반납완료를 요청한다")
    @Test
    void giveBack() {
        // given
        Rental rental = Rental.builder()
                .user(user)
                .userbook(userbook)
                .rentalStatus(RentalStatus.IN_PROGRESS)
                .build();
        Rental save = this.rentalRepository.save(rental);
        given(this.userHelper.findCurrentUser()).willReturn(owner);

        // when
        this.rentalService.giveBack(save.getId());

        // then
        Optional<Rental> byId = this.rentalRepository.findById(save.getId());
        assertTrue(byId.isPresent());

        rental = byId.get();
        assertNotNull(rental.getEndDate());
        assertNotEquals(rental.getUserbook().getTradeStatus(), TradeStatus.UNAVAILABLE);
    }

    @DisplayName("회원이 대여한 도서에 대해서 연장 신청을 한다")
    @Test
    void extension() {
        // given
        Rental rental = Rental.builder()
                .user(user)
                .userbook(userbook)
                .rentalStatus(RentalStatus.IN_PROGRESS)
                .build();
        rental.respond(true);
        Rental save = this.rentalRepository.save(rental);
        given(this.userHelper.findCurrentUser()).willReturn(user);

        // when
        Long extensionId = this.extensionService.extensionRental(save.getId());

        // then
        Optional<Extension> byId = this.extensionRepository.findById(extensionId);
        assertTrue(byId.isPresent());
    }
}