package com.e207.woojoobook.global.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

// 랜덤 시크릿 키를 생성하는 클래스
// TODO : 외부에서 관리되어야 함
public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated Key: " + base64Key);
    }
}