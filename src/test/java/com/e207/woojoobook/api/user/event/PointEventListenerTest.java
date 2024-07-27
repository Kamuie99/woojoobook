package com.e207.woojoobook.api.user.event;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.user.point.PointView;
import com.e207.woojoobook.domain.user.point.PointViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointEventListenerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointViewRepository pointViewRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @DisplayName("회원의 포인트 이벤트를 발행하면 결과가 반영된다. 초기 회원 포인트는 0")
    @Transactional
    @ParameterizedTest
    @MethodSource
    void handlePointEvent(PointHistory pointHistory) {
        // given
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .build();
        user = this.userRepository.save(user);

        // when
        PointEvent event = new PointEvent(user, pointHistory);
        this.applicationEventPublisher.publishEvent(event);

        // then
        Optional<PointView> byUserId = this.pointViewRepository.findByUserId(user.getId());
        assertThat(byUserId.isPresent()).isTrue();

        PointView pointView = byUserId.get();
        assertThat(pointView.getTotalPoint()).isEqualTo(pointHistory.getAmount());
    }

    private static Stream<Arguments> handlePointEvent() {
        return Stream.of(
                Arguments.of(PointHistory.ATTENDANCE),
                Arguments.of(PointHistory.BOOK_EXCHANGE),
                Arguments.of(PointHistory.BOOK_REGISTER),
                Arguments.of(PointHistory.BOOK_RENTAL),
                Arguments.of(PointHistory.USE_BOOK_RENTAL),
                Arguments.of(PointHistory.OVERDUE)
        );
    }
}