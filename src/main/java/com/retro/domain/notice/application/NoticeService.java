package com.retro.domain.notice.application;

import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.application.dto.NoticeCursorPageResponse;
import com.retro.domain.notice.application.dto.NoticeResponse;
import com.retro.domain.notice.application.event.NoticeEventPublisher;
import com.retro.domain.notice.domain.entity.Notice;
import com.retro.domain.notice.domain.entity.repository.NoticeRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.List;
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

  public NoticeCursorPageResponse getNotices(Long cursorId, int size) {
    final int pageSizePlusOne = size + 1;
    List<Notice> notices = findNoticesWithCursor(cursorId, pageSizePlusOne);

    boolean hasNext = hasMoreNotices(size, notices);
    if (hasNext) {
      notices = sliceNotices(size, notices);
    }

    List<NoticeResponse> responses = createNoticeResponses(notices);

    Long nextCursor = getNextCursor(hasNext, notices);
    return NoticeCursorPageResponse.of(responses, nextCursor, hasNext);
  }

  private List<Notice> findNoticesWithCursor(Long cursorId, int size) {
    return noticeRepository.findNoticesWithCursor(cursorId, size);
  }

  private boolean hasMoreNotices(int size, List<Notice> notices) {
    return notices.size() > size;
  }

  private List<Notice> sliceNotices(int size, List<Notice> notices) {
    return notices.subList(0, size);
  }

  private List<NoticeResponse> createNoticeResponses(List<Notice> notices) {
    return notices.stream()
        .map(NoticeResponse::from)
        .toList();
  }

  private Long getNextCursor(boolean hasNext, List<Notice> notices) {
    return hasNext ? notices.get(notices.size() - 1).getNoticeId() : null;
  }
}