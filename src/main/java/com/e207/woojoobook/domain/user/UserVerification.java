package com.e207.woojoobook.domain.user;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "userVerification", timeToLive = 300)
public class UserVerification implements Serializable {
	@Id
	private String id;
	private String email;
	private String verificationCode;
	private boolean isVerified;

	@Builder
	public UserVerification(String email, String verificationCode, boolean isVerified) {
		this.email = email;
		this.verificationCode = verificationCode;
		this.isVerified = isVerified;
	}

	public boolean verify(String verificationCode) {
		if (verificationCode.equals(this.verificationCode)) {
			this.isVerified = true;
			return true;
		}
		return false;
	}
}
