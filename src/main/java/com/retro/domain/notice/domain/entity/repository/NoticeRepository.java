package com.retro.domain.notice.domain.entity.repository;

import com.retro.domain.notice.domain.entity.Notice;
import java.util.Optional;

public interface NoticeRepository {

  public Notice createNotice(Notice notice);

  public Optional<Notice> getNotice(Long noticeId);
}
