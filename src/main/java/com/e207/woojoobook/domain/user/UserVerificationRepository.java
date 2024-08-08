package com.e207.woojoobook.domain.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserVerificationRepository extends CrudRepository<UserVerification, String> {
	Optional<UserVerification> findByEmail(String email);

}
