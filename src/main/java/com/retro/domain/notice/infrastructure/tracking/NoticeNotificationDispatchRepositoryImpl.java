package com.retro.domain.notice.infrastructure.tracking;

import com.retro.domain.notice.domain.entity.tracking.NoticeNotificationDispatch;
import com.retro.domain.notice.domain.entity.tracking.NoticeNotificationDispatchRepository;
import com.retro.domain.notice.infrastructure.jpa.tracking.NoticeNotificationDispatchJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeNotificationDispatchRepositoryImpl implements
    NoticeNotificationDispatchRepository {

  private final NoticeNotificationDispatchJpaRepository noticeNotificationDispatchJpaRepository;

  @Override
  public Optional<NoticeNotificationDispatch> findByNoticeId(Long noticeId) {
    return noticeNotificationDispatchJpaRepository.findByNoticeId(noticeId);
  }

  @Override
  public NoticeNotificationDispatch save(NoticeNotificationDispatch noticeNotificationDispatch) {
    return noticeNotificationDispatchJpaRepository.save(noticeNotificationDispatch);
  }
}
