package com.e207.woojoobook.domain.user.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointViewRepository extends JpaRepository<PointView, Long> {
    Optional<PointView> findByUserId(Long pointId);
}
