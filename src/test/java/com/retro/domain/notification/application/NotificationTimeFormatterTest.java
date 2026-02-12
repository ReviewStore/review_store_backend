package com.retro.domain.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTimeFormatterTest {

  private final NotificationTimeFormatter formatter = new NotificationTimeFormatter();

  @Test
  @DisplayName("1분 미만은 방금 전으로 표기한다")
  void formatJustNow() {
    LocalDateTime now = LocalDateTime.of(2026, 1, 28, 21, 26);

    String result = formatter.format(now.minusSeconds(20), now);

    assertThat(result).isEqualTo("방금 전");
  }

  @Test
  @DisplayName("1시간 미만은 분 단위 상대 시간으로 표기한다")
  void formatMinutesAgo() {
    LocalDateTime now = LocalDateTime.of(2026, 1, 28, 21, 26);

    String result = formatter.format(now.minusMinutes(30), now);

    assertThat(result).isEqualTo("30분 전");
  }

  @Test
  @DisplayName("24시간 미만은 시간 단위 상대 시간으로 표기한다")
  void formatHoursAgo() {
    LocalDateTime now = LocalDateTime.of(2026, 1, 28, 21, 26);

    String result = formatter.format(now.minusHours(10), now);

    assertThat(result).isEqualTo("10시간 전");
  }

  @Test
  @DisplayName("7일 미만은 일 단위 상대 시간으로 표기한다")
  void formatDaysAgo() {
    LocalDateTime now = LocalDateTime.of(2026, 1, 28, 21, 26);

    String result = formatter.format(now.minusDays(5), now);

    assertThat(result).isEqualTo("5일 전");
  }

  @Test
  @DisplayName("7일 이상은 M.dd 형식으로 표기한다")
  void formatAbsoluteDate() {
    LocalDateTime now = LocalDateTime.of(2026, 1, 28, 21, 26);

    String result = formatter.format(now.minusDays(20), now);

    assertThat(result).isEqualTo("1.08");
  }
}