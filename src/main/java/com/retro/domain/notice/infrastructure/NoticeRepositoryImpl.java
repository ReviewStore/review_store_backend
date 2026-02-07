package com.retro.domain.notice.infrastructure;

import com.retro.domain.notice.domain.entity.Notice;
import com.retro.domain.notice.domain.entity.repository.NoticeRepository;
import com.retro.domain.notice.infrastructure.jpa.NoticeJPARepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

  private final NoticeJPARepository noticeJPARepository;

  @Override
  public Notice createNotice(Notice notice) {
    return noticeJPARepository.save(notice);
  }

  @Override
  public Optional<Notice> getNotice(Long noticeId) {
    return noticeJPARepository.findById(noticeId);
  }
}
