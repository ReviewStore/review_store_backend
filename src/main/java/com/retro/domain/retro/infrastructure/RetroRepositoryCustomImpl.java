package com.retro.domain.retro.infrastructure;

import static com.retro.domain.retro.domain.entity.QRetro.retro;
import static com.retro.domain.retro.domain.entity.QRetroReport.retroReport;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.entity.RetroReport;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class RetroRepositoryCustomImpl implements RetroRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Retro> findByMemberIdWithCursor(Long memberId, Long cursorId, int size) {
    return jpaQueryFactory
        .selectFrom(retro)
        .where(
            retro.memberId.eq(memberId),
            cursorId != null ? retro.retroId.lt(cursorId) : null
        )
        .orderBy(retro.retroId.desc())
        .limit(size)
        .fetch();
  }


  @Override
  public Optional<RetroReport> existsReportByRetroAndReporter(Long retroId, Long reporterMemberId) {

    RetroReport report = jpaQueryFactory
        .selectFrom(retroReport)
        .from(retroReport)
        .where(
            retroReport.retro.retroId.eq(retroId),
            retroReport.reporterMemberId.eq(reporterMemberId)
        )
        .fetchOne();

    return Optional.ofNullable(report);
  }
}
