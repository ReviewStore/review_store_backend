package com.retro.domain.notice.application;

import com.retro.domain.notice.domain.entity.tracking.NoticeNotificationDispatch;
import com.retro.domain.notice.domain.entity.tracking.NoticeNotificationDispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeNotificationDispatchService {

  private final NoticeNotificationDispatchRepository noticeNotificationDispatchRepository;

  @Transactional
  public NoticeNotificationDispatch getOrCreate(Long noticeId) {
    return noticeNotificationDispatchRepository.findByNoticeId(noticeId)
        .orElseGet(() -> noticeNotificationDispatchRepository.save(
            NoticeNotificationDispatch.start(noticeId)));
  }

  @Transactional
  public void progress(Long noticeId, int processedCount, Long lastCursorMemberId) {
    NoticeNotificationDispatch dispatch = getOrCreate(noticeId);
    dispatch.progress(processedCount, lastCursorMemberId);
  }

  @Transactional
  public void complete(Long noticeId) {
    NoticeNotificationDispatch dispatch = getOrCreate(noticeId);
    dispatch.complete();
  }

  @Transactional
  public void fail(Long noticeId, String failedReason) {
    NoticeNotificationDispatch dispatch = getOrCreate(noticeId);
    dispatch.fail(failedReason);
  }
}
