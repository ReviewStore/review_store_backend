package com.retro.domain.notification.infrastructure;

import static com.retro.domain.notification.domain.entity.QNotification.notification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.notification.domain.entity.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Notification> findByMemberIdWithCursor(Long memberId, Long cursorId, int size) {
    return jpaQueryFactory
        .selectFrom(notification)
        .where(
            notification.memberId.eq(memberId),
            cursorId != null ? notification.notificationId.lt(cursorId) : null
        )
        .orderBy(notification.notificationId.desc())
        .limit(size)
        .fetch();
  }
}
