package com.e207.woojoobook.api.area;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.e207.woojoobook.domain.area.Area;
import com.e207.woojoobook.domain.area.AreaRepository;
import com.e207.woojoobook.domain.area.DongArea;
import com.e207.woojoobook.domain.area.DongId;
import com.e207.woojoobook.domain.area.GuArea;
import com.e207.woojoobook.domain.area.SiArea;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AreaServiceTest {

	@Autowired
	private AreaService areaService;
	@Autowired
	private EntityManager em;
	@MockBean
	private AreaRepository areaRepository;

	@DisplayName("지역코드로 지역 정보 조회 시 지역 코드의 길이가 올바르지 않으면 예외를 던진다.")
	@Test
	void When_NotValidAreaCodeLength_ThrowException() {
		// given
		String longAreaCode = "012345678910";
		String shortAreaCode = "012345";
		String validAreaCode = "0123456789";

		SiArea siArea = mock();
		GuArea guArea = mock();
		given(guArea.getGuId()).willReturn(mock());
		DongArea dongArea = mock();
		given(dongArea.getDongId()).willReturn(mock());

		given(areaRepository.findAreaByDongId(any())).willReturn(Optional.of(new Area(siArea, guArea, dongArea)));

		// when
		Executable longAreaCodeExecution = () -> areaService.findAreaByAreaCode(longAreaCode);
		Executable shortAreaCodeExecution = () -> areaService.findAreaByAreaCode(shortAreaCode);
		Executable validAreaCodeExecution = () -> areaService.findAreaByAreaCode(validAreaCode);

		// then
		assertThrows(IllegalArgumentException.class, longAreaCodeExecution);
		assertThrows(IllegalArgumentException.class, shortAreaCodeExecution);
		assertDoesNotThrow(validAreaCodeExecution);
	}

	@DisplayName("존재하지 않는 지역 코드로 검색 시 예외를 던진다.")
	@Test
	void When_NotExistAreaCode_ThrowException() {
		// given
		SiArea siArea = mock();
		GuArea guArea = mock();
		given(guArea.getGuId()).willReturn(mock());
		DongArea dongArea = mock();
		given(dongArea.getDongId()).willReturn(mock());

		String existDongCode = "01234";
		String notExistDongCode = "56789";

		String prefix = "00000";
		String existAreaCode = prefix + existDongCode;
		String notExistAreaCode = prefix + notExistDongCode;

		given(areaRepository.findAreaByDongId(any())).will(ans -> {
			DongId dongId = ans.getArgument(0);
			if (dongId.getDongCode().equals(existDongCode))
				return Optional.of(new Area(siArea, guArea, dongArea));
			else
				return Optional.empty();
		});

		// when
		Executable existCodeExecution = () -> areaService.findAreaByAreaCode(existAreaCode);
		Executable notExistCodeExecution = () -> areaService.findAreaByAreaCode(notExistAreaCode);

		// then
		assertDoesNotThrow(existCodeExecution);
		assertThrows(RuntimeException.class, notExistCodeExecution);
	}
}