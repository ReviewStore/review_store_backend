package com.retro.domain.notification.infrastructure;

import com.retro.domain.notification.domain.entity.Notification;
import java.util.List;

public interface NotificationRepositoryCustom {

  List<Notification> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);
}
