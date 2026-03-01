package com.retro.domain.member.infrastructure;

import static com.retro.domain.member.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Long> findMemberIdsWithCursor(Long cursorId, int size) {
    return jpaQueryFactory
        .select(member.id)
        .from(member)
        .where(cursorId != null ? member.id.gt(cursorId) : null)
        .orderBy(member.id.asc())
        .limit(size)
        .fetch();
  }
}
