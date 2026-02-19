package com.retro.domain.notification.application;

import static com.retro.domain.notification.domain.entity.NotificationMessage.READ_PERMISSION_EXPIRED_GUIDE_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.READ_PERMISSION_EXPIRED_GUIDE_TITLE;
import static com.retro.domain.notification.domain.entity.NotificationMessage.RETROSPECTIVE_BLINDED_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.RETROSPECTIVE_BLINDED_TITLE;
import static com.retro.domain.notification.domain.entity.NotificationMessage.WELCOME_CONTENT;
import static com.retro.domain.notification.domain.entity.NotificationMessage.WELCOME_TITLE;

import com.retro.domain.notification.application.dto.NotificationCursorPageResponse;
import com.retro.domain.notification.application.dto.NotificationResponse;
import com.retro.domain.notification.domain.entity.Notification;
import com.retro.domain.notification.domain.entity.NotificationType;
import com.retro.domain.notification.domain.repository.NotificationRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationTimeFormatter notificationTimeFormatter;

  @Transactional
  public void createWelcomeNotification(Long memberId) {
    Notification notification = Notification.of(memberId, NotificationType.WELCOME,
        WELCOME_TITLE.getMessage(),
        WELCOME_CONTENT.getMessage());
    notificationRepository.save(notification);
  }
// TODO 추후 공지 알림 기능 구현 시 사용(스프링 배치)
//  @Transactional
//  public void createNoticeNotificationForAllMembers() {
//    List<Long> memberIds = memberRepository.findAllMemberIds();
//    List<Notification> notifications = memberIds.stream()
//        .map(memberId -> Notification.of(memberId, NotificationType.NOTICE, noti,
//            NOTICE_CONTENT))
//        .toList();
//    notificationRepository.saveAll(notifications);
//  }

  @Transactional
  public void createReadPermissionGuideNotification(Long memberId) {
    Notification notification = Notification.of(memberId,
        NotificationType.READ_PERMISSION_EXPIRED_GUIDE,
        READ_PERMISSION_EXPIRED_GUIDE_TITLE.getMessage(),
        READ_PERMISSION_EXPIRED_GUIDE_CONTENT.getMessage());
    notificationRepository.save(notification);
  }

  @Transactional
  public void createRetroBlindedNotification(Long memberId) {
    Notification notification = Notification.of(memberId,
        NotificationType.BLIND,
        RETROSPECTIVE_BLINDED_TITLE.getMessage(),
        RETROSPECTIVE_BLINDED_CONTENT.getMessage());
    notificationRepository.save(notification);
  }

  public NotificationResponse getNotification(Long memberId, Long notificationId) {
    Notification notification = notificationRepository.findByIdAndMemberId(notificationId, memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

    return NotificationResponse.from(notification,
        notificationTimeFormatter.format(notification.getCreatedAt()));
  }

  public NotificationCursorPageResponse getNotifications(Long memberId, Long cursorId, int size) {
    List<Notification> notifications = getMyNotifications(memberId, cursorId, size);

    boolean hasNext = hasMoreNotifications(size, notifications);
    if (hasNext) {
      notifications = sliceNotifications(size, notifications);
    }

    List<NotificationResponse> responses = createNotificationResponses(notifications);

    Long nextCursor = getNextCursor(hasNext, notifications);
    return NotificationCursorPageResponse.of(responses, nextCursor, hasNext);
  }

  private List<NotificationResponse> createNotificationResponses(List<Notification> notifications) {
    return notifications.stream()
        .map(notification -> NotificationResponse.from(notification,
            notificationTimeFormatter.format(notification.getCreatedAt())))
        .toList();
  }

  private List<Notification> getMyNotifications(Long memberId, Long cursorId, int size) {
    final int pageSizePlusOne = size + 1;
    return notificationRepository.findByMemberIdWithCursor(memberId, cursorId,
        pageSizePlusOne);
  }

  private List<Notification> sliceNotifications(int size, List<Notification> notifications) {
    return notifications.subList(0, size);
  }

  private boolean hasMoreNotifications(int size, List<Notification> notifications) {
    return notifications.size() > size;
  }

  private Long getNextCursor(boolean hasNext, List<Notification> notifications) {
    return hasNext ? notifications.getLast().getNotificationId() : null;
  }


}
