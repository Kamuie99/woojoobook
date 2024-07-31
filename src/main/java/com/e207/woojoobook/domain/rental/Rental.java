package com.e207.woojoobook.domain.rental;

import java.time.LocalDateTime;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.userbook.Userbook;

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
public class Rental {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
	@ManyToOne
	private Userbook userbook;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private RentalStatus rentalStatus;
	private int extensionCount;

	@Builder
	public Rental(Long id, User user, Userbook userbook, RentalStatus rentalStatus) {
		this.user = user;
		this.userbook = userbook;
		this.rentalStatus = rentalStatus;
	}

	public void respond(boolean isApproved) {
		if (isApproved) {
			this.startDate = LocalDateTime.now();
			this.endDate = LocalDateTime.now().plusDays(7);
			this.rentalStatus = RentalStatus.IN_PROGRESS;
		} else {
			this.rentalStatus = RentalStatus.REJECTED;
		}
	}

	public void giveBack() {
		this.endDate = LocalDateTime.now();
		this.rentalStatus = RentalStatus.COMPLETED;
	}

	public void extension(boolean isApproved) {
		if (isApproved) {
			this.endDate = endDate.plusDays(7);
			this.extensionCount++;
		}
	}

	public void removeUser() {
		this.user = null;
	}

	public boolean isOffering() {
		return this.rentalStatus == RentalStatus.IN_PROGRESS && this.startDate == null;
	}
}
