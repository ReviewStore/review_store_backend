package com.retro.domain.retro.infrastructure;

import static com.retro.domain.retro.domain.entity.QKeyword.keyword;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retro.domain.retro.domain.entity.Keyword;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom {

  private static final int KEYWORD_LIMIT_COUNT = 10;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Keyword> findAllByContentContaining(String content) {

    return jpaQueryFactory
        .selectFrom(keyword)
        .where(keyword.name.contains(content))
        .orderBy(keyword.name.asc())
        .limit(KEYWORD_LIMIT_COUNT)
        .fetch();
  }
}
