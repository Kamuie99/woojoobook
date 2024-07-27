package com.e207.woojoobook.api.user;

import com.e207.woojoobook.domain.user.point.PointView;
import com.e207.woojoobook.domain.user.point.PointViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointViewRepository pointViewRepository;

    @Transactional(readOnly = true)
    public int getUserPoints(Long userId) {
        return pointViewRepository.findByUserId(userId)
                .map(PointView::getTotalPoint)
                .orElse(0);
    }
}
