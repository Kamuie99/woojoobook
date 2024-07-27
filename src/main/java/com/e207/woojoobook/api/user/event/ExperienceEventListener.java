package com.e207.woojoobook.api.user.event;

import com.e207.woojoobook.domain.user.experience.Experience;
import com.e207.woojoobook.domain.user.experience.ExperienceRepository;
import com.e207.woojoobook.domain.user.experience.ExperienceViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExperienceEventListener {
    private final ExperienceRepository experienceRepository;

    @EventListener
    public void handleExperienceEvent(ExperienceEvent event) {
        Experience experience = Experience.builder()
                .user(event.user())
                .history(event.history())
                .amount(event.history().getAmount())
                .build();
        this.experienceRepository.save(experience);
    }



}
