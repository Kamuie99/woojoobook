package com.e207.woojoobook.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSlaveRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
}
