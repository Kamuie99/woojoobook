package com.e207.woojoobook.api.service.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.e207.woojoobook.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // TODO : 예외처리
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.e207.woojoobook.domain.user.User user = this.userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}
