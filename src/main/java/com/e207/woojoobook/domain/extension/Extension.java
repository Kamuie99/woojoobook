package com.e207.woojoobook.domain.extension;

import static com.e207.woojoobook.domain.extension.ExtensionStatus.*;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.rental.Rental;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Extension {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Rental rental;
	private LocalDateTime createdAt;
	private ExtensionStatus extensionStatus;

	@Builder
	public Extension(Rental rental, LocalDateTime createdAt, ExtensionStatus extensionStatus) {
		this.rental = rental;
		this.createdAt = createdAt;
		this.extensionStatus = OFFERING;
	}

	public void respond(boolean isApproved) {
		this.extensionStatus = isApproved ? APPROVED : REJECTED;
	}
}
