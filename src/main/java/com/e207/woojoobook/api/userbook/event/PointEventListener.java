package com.e207.woojoobook.api.user.event;

import com.e207.woojoobook.domain.user.experience.Experience;
import com.e207.woojoobook.domain.user.point.Point;
import com.e207.woojoobook.domain.user.point.PointRepository;
import com.e207.woojoobook.domain.user.point.PointView;
import com.e207.woojoobook.domain.user.point.PointViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PointEventListener {
    private final PointRepository pointRepository;

    @EventListener
    public void handlePointEvent(PointEvent event) {
        Point point = Point.builder()
                .user(event.user())
                .history(event.history())
                .amount(event.history().getAmount())
                .build();
        this.pointRepository.save(point);
    }

}
