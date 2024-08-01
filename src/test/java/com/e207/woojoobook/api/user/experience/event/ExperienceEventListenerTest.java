package com.e207.woojoobook.api.user.experience.event;

import com.e207.woojoobook.api.userbook.event.ExperienceEvent;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;
import com.e207.woojoobook.domain.user.experience.ExperienceView;
import com.e207.woojoobook.domain.user.experience.ExperienceViewRepository;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ExperienceEventListenerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceViewRepository experienceViewRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @DisplayName("회원의 경험치 이벤트를 발행하면 결과가 반영된다. 초기 회원 경험치는 0")
    @Transactional
    @ParameterizedTest
    @MethodSource
    void handleExperienceEvent(ExperienceHistory experienceHistory) {
        // given
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .build();
        user = this.userRepository.save(user);

        // when
        ExperienceEvent event = new ExperienceEvent(user, experienceHistory);
        this.applicationEventPublisher.publishEvent(event);

        // then
        Optional<ExperienceView> byUserId = this.experienceViewRepository.findByUserId(user.getId());
        assertThat(byUserId.isPresent()).isTrue();

        ExperienceView experienceView = byUserId.get();
        assertThat(experienceView.getTotalExperience()).isEqualTo(experienceHistory.getAmount());
    }

    private static Stream<Arguments> handleExperienceEvent() {
        return Stream.of(
                Arguments.of(ExperienceHistory.ATTENDANCE),
                Arguments.of(ExperienceHistory.BOOK_EXCHANGE),
                Arguments.of(ExperienceHistory.BOOK_REGISTER),
                Arguments.of(ExperienceHistory.BOOK_RENTAL),
                Arguments.of(ExperienceHistory.DELETE_BOOK),
                Arguments.of(ExperienceHistory.OVERDUE)
        );
    }

}