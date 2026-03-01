package com.retro.domain.notice.infrastructure.jpa.tracking;

import com.retro.domain.notice.domain.entity.tracking.NoticeNotificationDispatch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeNotificationDispatchJpaRepository extends
    JpaRepository<NoticeNotificationDispatch, Long> {

  Optional<NoticeNotificationDispatch> findByNoticeId(Long noticeId);

}
