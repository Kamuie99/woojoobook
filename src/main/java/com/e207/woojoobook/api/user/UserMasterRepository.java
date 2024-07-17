package com.e207.woojoobook.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e207.woojoobook.domain.user.User;

public interface UserMasterRepository extends JpaRepository<User, Long> {
}
