package com.retro.domain.notice.domain.entity.tracking;

import java.util.Optional;

public interface NoticeNotificationDispatchRepository {

  Optional<NoticeNotificationDispatch> findByNoticeId(Long noticeId);

  NoticeNotificationDispatch save(NoticeNotificationDispatch noticeNotificationDispatch);
}
