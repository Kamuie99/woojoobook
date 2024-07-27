package com.e207.woojoobook.domain.user.experience;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExperienceViewRepository extends JpaRepository<ExperienceView, Long> {
    Optional<ExperienceView> findByUserId(Long userId);
}
