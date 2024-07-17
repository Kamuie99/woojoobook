package com.e207.woojoobook.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMasterRepository extends JpaRepository<User, Long> {
}
