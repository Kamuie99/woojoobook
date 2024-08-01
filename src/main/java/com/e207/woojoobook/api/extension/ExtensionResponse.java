package com.e207.woojoobook.api.extension;

import java.time.LocalDateTime;

import com.e207.woojoobook.api.rental.response.RentalResponse;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.extension.ExtensionStatus;

import lombok.Builder;

@Builder
public record ExtensionResponse(Long id, RentalResponse rentalResponse, LocalDateTime createdAt,
								ExtensionStatus status) {
	public static ExtensionResponse of(Extension extension) {
		return ExtensionResponse.builder()
			.id(extension.getId())
			.rentalResponse(RentalResponse.of(extension.getRental()))
			.createdAt(extension.getCreatedAt())
			.status(extension.getExtensionStatus())
			.build();
	}
}
