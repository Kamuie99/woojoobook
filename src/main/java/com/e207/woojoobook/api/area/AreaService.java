package com.e207.woojoobook.api.area;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.e207.woojoobook.api.area.request.DongAreaFindRequest;
import com.e207.woojoobook.api.area.request.GuAreaFindRequest;
import com.e207.woojoobook.api.area.response.AreaResponse;
import com.e207.woojoobook.api.area.response.DongAreaResponse;
import com.e207.woojoobook.api.area.response.GuAreaResponse;
import com.e207.woojoobook.api.area.response.SiAreaResponse;
import com.e207.woojoobook.domain.area.AreaRepository;
import com.e207.woojoobook.domain.area.DongId;
import com.e207.woojoobook.domain.area.GuArea;
import com.e207.woojoobook.domain.area.GuId;
import com.e207.woojoobook.domain.area.SiArea;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AreaService {
	private static final int AREA_CODE_LENGTH = 10;

	private final AreaRepository areaRepository;
	private final EntityManager em;

	@Transactional(readOnly = true)
	public List<SiAreaResponse> findSiAreaList() {
		return areaRepository.findSiArea().stream().map(SiAreaResponse::of).toList();
	}

	@Transactional(readOnly = true)
	public List<GuAreaResponse> findGuAreaList(GuAreaFindRequest request) {
		return areaRepository.findGuAreaBySiCode(request.siCode()).stream().map(GuAreaResponse::of).toList();
	}

	@Transactional(readOnly = true)
	public List<DongAreaResponse> findDongAreaList(DongAreaFindRequest request) {
		GuId guId = new GuId(em.getReference(SiArea.class, request.siCode()), request.guCode());
		return areaRepository.findDongAreaByGuId(guId).stream().map(DongAreaResponse::of).toList();
	}

	@Transactional(readOnly = true)
	public AreaResponse findAreaByAreaCode(String areaCode) {
		return areaRepository.findAreaByDongId(makeDongId(areaCode))
			.map(AreaResponse::of)
			.orElseThrow(() -> new RuntimeException("AreaCode가 존재하지 않을 때 던지는 예외"));
	}

	private DongId makeDongId(String areaCode) {
		Assert.isTrue(areaCode.length() == AREA_CODE_LENGTH, "지역 코드 길이가 올바르지 않습니다.");

		String siCode = areaCode.substring(0, 2);
		SiArea siAreaRef = em.getReference(SiArea.class, siCode);

		String guCode = areaCode.substring(2, 5);
		GuArea guAreaRef = em.getReference(GuArea.class, new GuId(siAreaRef, guCode));

		String dongCode = areaCode.substring(5);
		return new DongId(guAreaRef, dongCode);
	}
}