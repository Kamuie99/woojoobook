package com.e207.woojoobook.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

	private SecurityUtil() {
	}

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static String getCurrentUsername() {
		Authentication authentication = getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails)authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}
}
