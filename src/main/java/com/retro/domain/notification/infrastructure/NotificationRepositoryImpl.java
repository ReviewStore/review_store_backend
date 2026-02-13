package com.retro.domain.notification.infrastructure;

import com.retro.domain.notification.domain.entity.Notification;
import com.retro.domain.notification.domain.repository.NotificationRepository;
import com.retro.domain.notification.infrastructure.jpa.NotificationJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationJpaRepository notificationJpaRepository;
  private final NotificationRepositoryCustom notificationRepositoryCustom;


  @Override
  public Notification save(Notification notification) {
    return notificationJpaRepository.save(notification);
  }

  @Override
  public List<Notification> saveAll(List<Notification> notifications) {
    return notificationJpaRepository.saveAll(notifications);
  }

  @Override
  public Optional<Notification> findByIdAndMemberId(Long notificationId, Long memberId) {
    return notificationJpaRepository.findByNotificationIdAndMemberId(notificationId, memberId);
  }

  @Override
  public List<Notification> findByMemberIdWithCursor(Long memberId, Long cursorId, int size) {
    return notificationRepositoryCustom.findByMemberIdWithCursor(memberId, cursorId, size);
  }
}
