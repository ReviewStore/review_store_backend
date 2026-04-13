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
  public List<Retro> searchCommunityFeed(String keyword, String position, String interviewRound,
      Long cursorId, int size) {
    return jpaQueryFactory
        .selectFrom(retro)
        .join(member).on(retro.memberId.eq(member.id))
        .where(buildSearchPredicate(keyword, position, interviewRound, cursorId))
        .orderBy(retro.retroId.desc())
        .limit(size)
        .fetch();
  }

  private BooleanBuilder buildSearchPredicate(String keyword, String position,
      String interviewRound, Long cursorId) {
    BooleanBuilder predicate = new BooleanBuilder();
    predicate.and(member.isPublic.isTrue());
    predicate.and(retro.blinded.isFalse());

    if (isBlank(keyword)) {
      predicate.and(
          retro.companyName.containsIgnoreCase(keyword)
              .or(retro.interviewTags.containsIgnoreCase(keyword))
      );
    }
    if (isBlank(position)) {
      predicate.and(retro.position.containsIgnoreCase(position));
    }
    if (isBlank(interviewRound)) {
      predicate.and(retro.interviewRound.eq(interviewRound));
    }
    if (!Objects.isNull(cursorId)) {
      predicate.and(retro.retroId.lt(cursorId));
    }

    return predicate;
  }

  private boolean isBlank(String value) {
    return Objects.nonNull(value) && !value.isBlank();
  }
}
