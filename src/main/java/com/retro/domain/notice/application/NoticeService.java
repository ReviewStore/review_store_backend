package com.retro.domain.notice.application;

import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.application.event.NoticeEventPublisher;
import com.retro.domain.notice.domain.entity.Notice;
import com.retro.domain.notice.domain.entity.repository.NoticeRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final NoticeEventPublisher noticeEventPublisher;


  @Transactional
  public NoticeCreateResponse createNotice(NoticeCreateRequest request) {
    Notice notice = request.toEntity();
    Notice savedNotice = noticeRepository.createNotice(notice);
    noticeEventPublisher.publishNoticeCreatedEvent(savedNotice.getNoticeId());
    return NoticeCreateResponse.of(savedNotice.getNoticeId(),
        savedNotice.getCreatedAt());
  }

  public Notice getNotice(Long noticeId) {
    return noticeRepository.getNotice(noticeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
  }
}