package com.retro.domain.notice.domain.entity.tracking;

import com.retro.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice_notification_dispatches", uniqueConstraints = {
    @UniqueConstraint(name = "uk_notice_notification_dispatch_notice_id", columnNames = "notice_id")
})
public class NoticeNotificationDispatch extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long noticeNotificationDispatchId;

  @Column(name = "notice_id", nullable = false)
  private Long noticeId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NoticeNotificationDispatchStatus status;

  @Column(nullable = false)
  private int processedCount;

  private Long lastCursorMemberId;

  @Column(length = 1000)
  private String failedReason;

  @Builder(access = AccessLevel.PRIVATE)
  private NoticeNotificationDispatch(Long noticeId, NoticeNotificationDispatchStatus status,
      int processedCount,
      Long lastCursorMemberId, String failedReason) {
    this.noticeId = noticeId;
    this.status = status;
    this.processedCount = processedCount;
    this.lastCursorMemberId = lastCursorMemberId;
    this.failedReason = failedReason;
  }

  public static NoticeNotificationDispatch start(Long noticeId) {
    return NoticeNotificationDispatch.builder()
        .noticeId(noticeId)
        .status(NoticeNotificationDispatchStatus.IN_PROGRESS)
        .processedCount(0)
        .build();
  }

  public void progress(int processedCount, Long lastCursorMemberId) {
    this.status = NoticeNotificationDispatchStatus.IN_PROGRESS;
    this.processedCount = this.processedCount + processedCount;
    this.lastCursorMemberId = lastCursorMemberId;
    this.failedReason = null;
  }

  public void complete() {
    this.status = NoticeNotificationDispatchStatus.COMPLETED;
    this.failedReason = null;
  }

  public void fail(String failedReason) {
    this.status = NoticeNotificationDispatchStatus.FAILED;
    this.failedReason = failedReason;
  }
}