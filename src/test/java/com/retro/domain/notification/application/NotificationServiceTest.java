package com.retro.domain.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.retro.domain.notification.application.dto.NotificationCursorPageResponse;
import com.retro.domain.notification.application.dto.NotificationResponse;
import com.retro.domain.notification.domain.entity.Notification;
import com.retro.domain.notification.domain.entity.NotificationType;
import com.retro.domain.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private NotificationTimeFormatter notificationTimeFormatter;
  @InjectMocks
  private NotificationService notificationService;

  @Test
  @DisplayName("회원가입 시 웰컴 알림을 생성한다")
  void createWelcomeNotification() {
    // given
    Long memberId = 1L;

    notificationService.createWelcomeNotification(memberId);

    verify(notificationRepository).save(any(Notification.class));
  }

  @Test
  @DisplayName("내 알림을 단건 조회한다")
  void getNotification() {
    NotificationService notificationService = new NotificationService(notificationRepository,
        notificationTimeFormatter);
    Notification notification = Notification.of(1L, NotificationType.WELCOME, "제목", "내용");
    ReflectionTestUtils.setField(notification, "notificationId", 10L);
    ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now().minusMinutes(5));
    when(notificationRepository.findByIdAndMemberId(10L, 1L)).thenReturn(Optional.of(notification));
    when(notificationTimeFormatter.format(notification.getCreatedAt()))
        .thenReturn("5분 전");

    NotificationResponse response = notificationService.getNotification(1L, 10L);

    assertThat(response.notificationId()).isEqualTo(10L);
    assertThat(response.displayTime()).isEqualTo("5분 전");
  }

  @Test
  @DisplayName("내 알림 목록을 커서 기반으로 조회한다")
  void getNotifications() {
    NotificationService notificationService = new NotificationService(notificationRepository,
        notificationTimeFormatter);

    Notification first = Notification.of(1L, NotificationType.NOTICE, "제목1", "내용1");
    Notification second = Notification.of(1L, NotificationType.NOTICE, "제목2", "내용2");
    Notification third = Notification.of(1L, NotificationType.NOTICE, "제목3", "내용3");
    ReflectionTestUtils.setField(first, "notificationId", 30L);
    ReflectionTestUtils.setField(second, "notificationId", 29L);
    ReflectionTestUtils.setField(third, "notificationId", 28L);
    ReflectionTestUtils.setField(first, "createdAt", LocalDateTime.now().minusMinutes(1));
    ReflectionTestUtils.setField(second, "createdAt", LocalDateTime.now().minusMinutes(2));
    ReflectionTestUtils.setField(third, "createdAt", LocalDateTime.now().minusMinutes(3));

    when(notificationRepository.findByMemberIdWithCursor(1L, null, 3)).thenReturn(
        List.of(first, second, third));

    NotificationCursorPageResponse response = notificationService.getNotifications(1L, null, 2);

    assertThat(response.notifications()).hasSize(2);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isEqualTo(29L);
  }
}