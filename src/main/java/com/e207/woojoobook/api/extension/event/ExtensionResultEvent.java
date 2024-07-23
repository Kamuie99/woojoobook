package com.e207.woojoobook.api.extension.event;

import com.e207.woojoobook.domain.extension.ExtensionStatus;
import com.e207.woojoobook.domain.rental.Rental;

public record ExtensionResultEvent(Rental rental, ExtensionStatus status) {
}
