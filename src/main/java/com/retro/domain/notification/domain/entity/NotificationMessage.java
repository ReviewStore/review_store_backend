package com.retro.domain.notification.domain.entity;

import lombok.Getter;

@Getter
public enum NotificationMessage {
  WELCOME_TITLE("웰컴 알림"),
  WELCOME_CONTENT("환영합니다! 선물로 드린 5장의 열람권으로 다른 분들의 생생한 면접 회고를 먼저 읽어보세요."),
  NOTICE_TITLE("공지사항"),
  NOTICE_CONTENT("새로운 기능이 추가되었어요! 회고집이 어떻게 바뀌었는지 확인해보세요."),
  READ_PERMISSION_EXPIRED_GUIDE_TITLE("열람권 소진 예고"),
  READ_PERMISSION_EXPIRED_GUIDE_CONTENT("열람권이 1장 남았습니다! 계속해서 피드를 읽고 싶다면 나의 회고를 공유해주세요."),
  RETROSPECTIVE_BLINDED_TITLE("게시글 블라인드"),
  RETROSPECTIVE_BLINDED_CONTENT("작성하신 회고가 신고 누적으로 인해 블라인드 처리되었습니다.");
  private final String message;

  NotificationMessage(String message) {
    this.message = message;
  }
}
