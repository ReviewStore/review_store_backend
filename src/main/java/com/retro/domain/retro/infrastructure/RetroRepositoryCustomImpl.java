package com.retro.domain.retro.infrastructure;

import static com.retro.domain.member.domain.entity.QMember.member;
import static com.retro.domain.retro.domain.entity.QRetro.retro;
import static com.retro.domain.retro.domain.entity.QRetroReport.retroReport;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.entity.RetroReport;
import java.util.List;
import java.util.Objects;
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

  @Override
  public List<Retro> findPublicRetrosWithCursor(Long cursorId, int size) {
    return jpaQueryFactory
        .selectFrom(retro)
        .join(member).on(retro.memberId.eq(member.id))
        .where(buildPublicFeedPredicate(cursorId))
        .orderBy(retro.retroId.desc())
        .limit(size)
        .fetch();
  }

  private BooleanBuilder buildPublicFeedPredicate(Long cursorId) {
    BooleanBuilder predicate = new BooleanBuilder();
    predicate.and(member.isPublic.isTrue());
    predicate.and(retro.blinded.isFalse());

    return appendCursorCondition(predicate, cursorId);
  }

  private BooleanBuilder appendCursorCondition(BooleanBuilder predicate, Long cursorId) {
    if (Objects.isNull(cursorId)) {
      return predicate;
    }

    predicate.and(retro.retroId.lt(cursorId));
    return predicate;
  }
}