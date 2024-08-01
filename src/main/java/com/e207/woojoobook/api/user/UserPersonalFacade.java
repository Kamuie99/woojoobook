package com.e207.woojoobook.api.user;

import com.e207.woojoobook.domain.user.point.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserPersonalFacade {

    private final PointService pointService;
    private final ExperienceService experienceService;

    @Transactional(readOnly = true)
    public int getUserPoints(Long userId) {
        return this.pointService.getUserPoints(userId);
    }

    @Transactional(readOnly = true)
    public int getUserExperience(Long userId) {
        return this.experienceService.getUserExperience(userId);
    }

    @Transactional(readOnly = true)
    public boolean checkPointToRental(Long userId) {
        int userPoints = this.pointService.getUserPoints(userId);

        return userPoints >= Math.abs(PointHistory.USE_BOOK_RENTAL.getAmount());
    }
}