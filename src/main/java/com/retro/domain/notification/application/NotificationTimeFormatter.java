package com.retro.domain.notification.application;

import com.retro.domain.notification.domain.entity.TimeAgoText;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class NotificationTimeFormatter {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.dd");

  public String format(LocalDateTime createdAt) {
    return format(createdAt, LocalDateTime.now());
  }

  String format(LocalDateTime createdAt, LocalDateTime now) {
    Duration duration = Duration.between(createdAt, now);

    if (isJustNow(duration)) {
      return TimeAgoText.JUST_NOW.value();
    }
    if (isWithinMinutes(duration)) {
      return duration.toMinutes() + TimeAgoText.MINUTES_AGO.value();
    }
    if (isWithinHours(duration)) {
      return duration.toHours() + TimeAgoText.HOURS_AGO.value();
    }
    if (isWithinDays(duration)) {
      return duration.toDays() + TimeAgoText.DAYS_AGO.value();
    }
    return createdAt.format(DATE_FORMATTER);
  }

  private boolean isJustNow(Duration duration) {
    return duration.toMinutes() < 1;
  }

  private boolean isWithinMinutes(Duration duration) {
    return duration.toHours() < 1;
  }

  private boolean isWithinHours(Duration duration) {
    return duration.toDays() < 1;
  }

  private boolean isWithinDays(Duration duration) {
    return duration.toDays() < 7;
  }


}
