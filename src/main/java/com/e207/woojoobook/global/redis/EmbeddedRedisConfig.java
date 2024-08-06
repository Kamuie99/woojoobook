package com.e207.woojoobook.global.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Profile("!prod")
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() throws IOException {
		int port = isRedisRunning() ? findAvailablePort() : redisPort;
		redisServer = new RedisServer(port);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() throws IOException {
		if (redisServer != null && redisServer.isActive()) {
			redisServer.stop();
		}
	}
	/**
	 * Embedded Redis가 현재 실행중인지 확인
	 */
	private boolean isRedisRunning() throws IOException {
		return isRunning(executeNetstatCommand(redisPort));
	}

	/**
	 * 현재 PC/서버에서 사용가능한 포트 조회
	 */
	public int findAvailablePort() throws IOException {
		for (int port = 10000; port <= 65535; port++) {
			Process process = executeNetstatCommand(port);
			if (!isRunning(process)) {
				return port;
			}
		}

		throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
	}

	/**
	 * 해당 port를 사용중인 프로세스 확인하는 netstat 명령어 실행
	 */
	private Process executeNetstatCommand(int port) throws IOException {
		String command = String.format("netstat -nao | findstr :%d", port);
		String[] cmd = {"cmd.exe", "/c", command};
		return Runtime.getRuntime().exec(cmd);
	}

	/**
	 * 해당 Process가 현재 실행중인지 확인
	 */
	private boolean isRunning(Process process) {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}
		} catch (Exception e) {
			// Handle exception
		}

		return !StringUtils.isEmpty(pidInfo.toString());
	}
}