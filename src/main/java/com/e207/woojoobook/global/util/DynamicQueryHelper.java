package com.e207.woojoobook.global.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

@Component
public class DynamicQueryHelper {
	public boolean hasField(Class<?> entityClass, String fieldName) {
		for (var field : entityClass.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				return true;
			}
		}

		return false;
	}

	public OrderSpecifier[] generateFieldSort(Class<?> entityClass, Sort sort, String variable) {
		PathBuilder pathBuilder = new PathBuilder(entityClass, variable);

		return sort.stream()
			.peek(order -> {
				// TODO 예외 처리
				if (!hasField(entityClass, order.getProperty())) {
					throw new RuntimeException("필드가 존재하지 않을 때 던지는 예외");
				}
			})
			.map(order -> {
				Order direction = order.isAscending() ? Order.ASC : Order.DESC;
				return new OrderSpecifier(direction, pathBuilder.get(order.getProperty()));
			})
			.toArray(OrderSpecifier[]::new);
	}
}
