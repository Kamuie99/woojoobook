package com.e207.woojoobook.domain.extension;

import com.e207.woojoobook.domain.rental.Rental;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        this.extensionStatus = extensionStatus;
    }

    public void respond(boolean isApproved) {
        this.extensionStatus = isApproved ? ExtensionStatus.APPROVED : ExtensionStatus.REJECTED;
    }
}
