package com.e207.woojoobook.domain.area;

import static com.e207.woojoobook.domain.area.QDongArea.*;
import static com.e207.woojoobook.domain.area.QGuArea.*;
import static com.e207.woojoobook.domain.area.QSiArea.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class AreaRepository {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	public AreaRepository(EntityManager em) {
		this.em = em;
		this.queryFactory = new JPAQueryFactory(this.em);
	}

	public List<SiArea> findSiArea() {
		return queryFactory.selectFrom(siArea).fetch();
	}

	public List<GuArea> findGuAreaBySiCode(String siCode) {
		return queryFactory.selectFrom(guArea).where(guArea.guId.siArea.siCode.eq(siCode)).fetch();
	}

	public List<DongArea> findDongAreaByGuId(GuId guId) {
		return queryFactory.selectFrom(dongArea)
			.join(dongArea.dongId.guArea, guArea)
			.where(guArea.guId.eq(guId))
			.fetch();
	}

	public Optional<Area> findAreaByDongId(DongId dongId) {
		return queryFactory.select(new QArea(siArea, guArea, dongArea))
			.from(dongArea)
			.join(dongArea.dongId.guArea, guArea)
			.join(guArea.guId.siArea, siArea)
			.where(dongArea.dongId.eq(dongId))
			.stream()
			.findAny();
	}
}
