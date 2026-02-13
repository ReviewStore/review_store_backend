package com.retro.domain.notification.domain.entity;

import com.retro.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long notificationId;

  @Column(nullable = false)
  private Long memberId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Builder(access = AccessLevel.PRIVATE)
  private Notification(Long memberId, NotificationType type, String title, String content) {
    this.memberId = memberId;
    this.type = type;
    this.title = title;
    this.content = content;
  }

  public static Notification of(Long memberId, NotificationType type, String title,
      String content) {
    return Notification.builder()
        .memberId(memberId)
        .type(type)
        .title(title)
        .content(content)
        .build();
  }
}