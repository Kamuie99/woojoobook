package com.e207.woojoobook.global.config.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "database")
public class DataSourceProperties {
	private String url;
	private String username;
	private String password;
	private final Map<String, Slave> slave = new HashMap<>();

	@Getter
	@Setter
	public static class Slave {
		private String name;
		private String url;
		private String username;
		private String password;
	}
}

