package com.retro.domain.notification.domain.repository;

import com.retro.domain.notification.domain.entity.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  Notification save(Notification notification);

  List<Notification> saveAll(List<Notification> notifications);

  Optional<Notification> findByIdAndMemberId(Long notificationId, Long memberId);

  List<Notification> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);
}