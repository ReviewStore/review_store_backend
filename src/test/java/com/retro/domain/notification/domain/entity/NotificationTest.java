package com.retro.domain.notification.domain.entity;

import static com.retro.domain.notification.domain.entity.NotificationMessage.NOTICE_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.NOTICE_TITLE;
import static com.retro.domain.notification.domain.entity.NotificationMessage.READ_PERMISSION_EXPIRED_GUIDE_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.READ_PERMISSION_EXPIRED_GUIDE_TITLE;
import static com.retro.domain.notification.domain.entity.NotificationMessage.RETROSPECTIVE_BLINDED_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.RETROSPECTIVE_BLINDED_TITLE;
import static com.retro.domain.notification.domain.entity.NotificationMessage.WELCOME_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.WELCOME_TITLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTest {

  @Test
  @DisplayName("WELCOME 타입 알림을 생성한다")
  void createWelcomeNotification() {

    Notification notification = Notification.of(1L, NotificationType.WELCOME,
        WELCOME_TITLE.getMessage(), WELCOME_CONTENT.getMessage());

    assertThat(notification.getType()).isEqualTo(NotificationType.WELCOME);
    assertThat(notification.getTitle()).isEqualTo("웰컴 알림");
    assertThat(notification.getContent()).isEqualTo(
        "환영합니다! 선물로 드린 5장의 열람권으로 다른 분들의 생생한 면접 회고를 먼저 읽어보세요.");
  }

  @Test
  @DisplayName("NOTICE 타입 알림을 생성한다")
  void createNoticeNotification() {

    Notification notification = Notification.of(1L, NotificationType.NOTICE,
        NOTICE_TITLE.getMessage(), NOTICE_CONTENT.getMessage());

    assertThat(notification.getType()).isEqualTo(NotificationType.NOTICE);
    assertThat(notification.getTitle()).isEqualTo("공지사항");
    assertThat(notification.getContent()).isEqualTo("새로운 기능이 추가되었어요! 회고집이 어떻게 바뀌었는지 확인해보세요.");
  }

  @Test
  @DisplayName("READ_PERMISSION_GUIDE 타입 알림을 생성한다")
  void createReadPermissionGuideNotification() {

    Notification notification = Notification.of(1L, NotificationType.READ_PERMISSION_EXPIRED_GUIDE,
        READ_PERMISSION_EXPIRED_GUIDE_TITLE.getMessage(),
        READ_PERMISSION_EXPIRED_GUIDE_CONTENT.getMessage());

    assertThat(notification.getType()).isEqualTo(NotificationType.READ_PERMISSION_EXPIRED_GUIDE);
    assertThat(notification.getTitle()).isEqualTo("열람권 소진 예고");
    assertThat(notification.getContent()).isEqualTo(
        "열람권이 1장 남았습니다! 계속해서 피드를 읽고 싶다면 나의 회고를 공유해주세요.");
  }

  @Test
  @DisplayName("BLIND 타입 알림을 생성한다")
  void createBlindNotification() {

    Notification notification = Notification.of(1L, NotificationType.BLIND,
        RETROSPECTIVE_BLINDED_TITLE.getMessage(), RETROSPECTIVE_BLINDED_CONTENT.getMessage());
    assertThat(notification.getType()).isEqualTo(NotificationType.BLIND);
    assertThat(notification.getTitle()).isEqualTo("게시글 블라인드");
    assertThat(notification.getContent()).isEqualTo("작성하신 회고가 신고 누적으로 인해 블라인드 처리되었습니다.");
  }
}