package com.e207.woojoobook.api.user;

import com.e207.woojoobook.domain.user.experience.ExperienceView;
import com.e207.woojoobook.domain.user.experience.ExperienceViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ExperienceService {

    private final ExperienceViewRepository experienceViewRepository;

    @Transactional(readOnly = true)
    public int getUserExperience(Long userId) {
        return experienceViewRepository.findByUserId(userId)
                .map(ExperienceView::getTotalExperience)
                .orElse(0);
    }
}
