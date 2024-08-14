package com.e207.woojoobook.domain.rental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.e207.woojoobook.api.userbook.event.ExperienceEvent;
import com.e207.woojoobook.api.userbook.event.PointEvent;
import com.e207.woojoobook.domain.extension.Extension;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;
import com.e207.woojoobook.domain.user.point.PointHistory;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.domain.userbook.Userbook;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Rental extends AbstractAggregateRoot<Rental> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Userbook userbook;
    @OneToMany(mappedBy = "rental", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Extension> extensions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;
    private int extensionCount;

    @Builder
    public Rental(Long id, User user, Userbook userbook, RentalStatus rentalStatus) {
        this.user = user;
        this.userbook = userbook;
        this.rentalStatus = rentalStatus;
    }

    public List<Extension> getExtensions() {
        if (extensions == null) {
            extensions = new ArrayList<>();
        }
        return extensions;
    }

    public void respond(boolean isApproved) {
        if (!this.userbook.isAvailable() || !isApproved ||
                this.rentalStatus != RentalStatus.OFFERING) {
            this.rentalStatus = RentalStatus.REJECTED;
            return;
        }
        this.startDate = LocalDateTime.now();
        this.endDate = LocalDateTime.now().plusDays(7);
        this.rentalStatus = RentalStatus.IN_PROGRESS;
        this.userbook.updateTradeStatus(TradeStatus.RENTED);
        registerApproveEvent();
    }

    public void giveBack() {
        if (extensions != null) {
            extensions.clear();
        }

        this.endDate = LocalDateTime.now();
        this.rentalStatus = RentalStatus.COMPLETED;
    }

    public void extension(boolean isApproved) {
        if (isApproved) {
            this.endDate = endDate.plusDays(7);
            this.extensionCount++;
        }
    }

    public void removeUser(User user) {
        this.user = user;
    }

    public boolean isOffering() {
        return this.rentalStatus == RentalStatus.IN_PROGRESS && this.startDate == null;
    }

    public boolean isSameOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    private void registerApproveEvent() {
        this.registerEvent(new PointEvent(user, PointHistory.USE_BOOK_RENTAL));
        this.registerEvent(new PointEvent(userbook.getUser(), PointHistory.BOOK_RENTAL));
        this.registerEvent(new ExperienceEvent(userbook.getUser(), ExperienceHistory.BOOK_RENTAL));
        this.registerEvent(new ExperienceEvent(user, ExperienceHistory.BOOK_RENTAL));
    }
}
