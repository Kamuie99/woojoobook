package com.e207.woojoobook.api.userbook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.userbook.request.UserbookPageFindRequest;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.domain.userbook.UserbookFindCondition;
import com.e207.woojoobook.domain.userbook.UserbookRepository;

@Service
public class UserbookService {

	private final Integer MAX_AREA_CODE_SIZE;
	private final UserbookRepository userbookSlaveRepository;

	public UserbookService(@Value("${userbook.search.ereacode.count}") Integer MAX_AREA_CODE_SIZE,
		UserbookRepository userbookSlaveRepository) {
		this.MAX_AREA_CODE_SIZE = MAX_AREA_CODE_SIZE;
		this.userbookSlaveRepository = userbookSlaveRepository;
	}

	@Transactional(readOnly = true)
	public Page<UserbookResponse> findUserbookPageList(UserbookPageFindRequest request, Pageable pageable) {
		// TODO: 예외 처리
		if (request.areaCodeList().size() > MAX_AREA_CODE_SIZE) {
			throw new RuntimeException("지역 선택이 초과 했을 때 던지는 에러");
		}

		var contents = this.userbookSlaveRepository.findUserbookList(UserbookFindCondition.of(request), pageable);
		var totalCount = this.userbookSlaveRepository.countUserbook(UserbookFindCondition.of(request));

		return new PageImpl<>(contents, pageable, totalCount).map(UserbookResponse::of);
	}
}
