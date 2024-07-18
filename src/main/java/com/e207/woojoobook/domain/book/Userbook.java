package com.e207.woojoobook.domain.book;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import com.e207.woojoobook.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
public class Userbook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Book book;

	@ManyToOne
	private User user;

	@ElementCollection(targetClass = RegisterType.class)
	@Enumerated(EnumType.STRING)
	private Set<RegisterType> registerType;

	@ElementCollection(targetClass = TradeStatus.class)
	@Enumerated(EnumType.STRING)
	private Set<TradeStatus> tradeStatus;

	@Enumerated(EnumType.STRING)
	private QualityStatus qualityStatus;

	@Builder
	private Userbook(Long id, Book book, User user, Set<RegisterType> registerType, Set<TradeStatus> tradeStatus,
		QualityStatus qualityStatus) {
		this.id = id;
		this.book = book;
		this.user = user;
		this.registerType = registerType;
		this.tradeStatus = tradeStatus;
		this.qualityStatus = qualityStatus;
	}
}