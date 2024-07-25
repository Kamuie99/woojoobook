package com.e207.woojoobook.global.config.db;

import java.util.List;

public class CircularList<T> {
	private final List<T> list;
	private Integer counter = 0;

	public CircularList(List<T> list) {
		this.list = list;
	}

	public T getOne() {
		// TODO <jhl221123> slave scale-out
		// if (counter + 1 >= list.size()) {
		// 	counter = -1;
		// }
		// return list.get(++counter);
		return list.get(counter);
	}
}
