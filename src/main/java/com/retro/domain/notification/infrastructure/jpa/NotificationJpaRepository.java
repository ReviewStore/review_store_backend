package com.retro.domain.notification.infrastructure.jpa;

import com.retro.domain.notification.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByNotificationIdAndMemberId(Long notificationId, Long memberId);
}
