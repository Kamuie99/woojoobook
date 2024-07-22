package com.e207.woojoobook.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
	Optional<User> findByEmail(String email);
}
