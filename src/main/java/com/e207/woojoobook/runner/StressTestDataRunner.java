package com.e207.woojoobook.runner;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.e207.woojoobook.WoojoobookApplication;
import com.e207.woojoobook.runner.util.StressDataGenerator;

public class StressTestDataRunner {

	public static void main(String[] args) throws IOException {
		ApplicationContext context = new SpringApplicationBuilder(WoojoobookApplication.class)
			.profiles("auth", "stress", "stress-data")
			.run(args);

		context.getBean(StressDataGenerator.class).generateData();

		SpringApplication.exit(context, () -> 0);
	}
}
