package com.e207.woojoobook.api.area;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.area.request.DongAreaFindRequest;
import com.e207.woojoobook.api.area.request.GuAreaFindRequest;
import com.e207.woojoobook.api.area.response.AreaResponse;
import com.e207.woojoobook.api.area.response.DongAreaListResponse;
import com.e207.woojoobook.api.area.response.GuAreaListResponse;
import com.e207.woojoobook.api.area.response.SiAreaListResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/area")
@RestController
public class AreaController {

	private final AreaService areaService;

	@GetMapping
	public ResponseEntity<AreaResponse> findAreaByAreaCode(@NotNull String areaCode) {
		return ResponseEntity.ok(areaService.findAreaByAreaCode(areaCode));
	}

	@GetMapping("/si")
	public ResponseEntity<SiAreaListResponse> findSiAreaList() {
		return ResponseEntity.ok(new SiAreaListResponse(areaService.findSiAreaList()));
	}

	@GetMapping("/gu")
	public ResponseEntity<GuAreaListResponse> findGuAreaList(@Valid @ModelAttribute GuAreaFindRequest request) {
		return ResponseEntity.ok(new GuAreaListResponse(areaService.findGuAreaList(request)));
	}

	@GetMapping("/dong")
	public ResponseEntity<DongAreaListResponse> findDongAreaList(@Valid @ModelAttribute DongAreaFindRequest request) {
		return ResponseEntity.ok(new DongAreaListResponse(areaService.findDongAreaList(request)));
	}
}
