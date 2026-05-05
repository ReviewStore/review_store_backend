package com.retro.domain.notice.infrastructure;

import static com.retro.domain.notice.domain.entity.QNotice.notice;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.notice.domain.entity.Notice;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Notice> findNoticesWithCursor(Long cursorId, int size) {
    return jpaQueryFactory
        .selectFrom(notice)
        .where(cursorId != null ? notice.noticeId.lt(cursorId) : null)
        .orderBy(notice.noticeId.desc())
        .limit(size)
        .fetch();
  }
}

