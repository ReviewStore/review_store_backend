package com.retro.domain.retro.infrastructure;

import static com.retro.domain.retro.domain.entity.QRetro.retro;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.retro.domain.entity.Retro;
import java.util.List;
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
}
