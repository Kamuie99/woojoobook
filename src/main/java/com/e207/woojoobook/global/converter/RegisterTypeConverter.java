package com.e207.woojoobook.global.converter;

import org.springframework.core.convert.converter.Converter;

import com.e207.woojoobook.domain.userbook.RegisterType;

public class RegisterTypeConverter implements Converter<String, RegisterType> {
	@Override
	public RegisterType convert(String source) {
		return RegisterType.valueOf(source);
	}
}
