package com.e207.woojoobook.api.userbook.event;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.experience.ExperienceHistory;

public record ExperienceEvent(User user, ExperienceHistory history) {
}
