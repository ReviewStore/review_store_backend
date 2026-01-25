package com.retro.domain.retro.infrastructure;

import com.retro.domain.retro.domain.entity.Keyword;
import com.retro.domain.retro.domain.repository.KeywordRepository;
import com.retro.domain.retro.infrastructure.jpa.KeywordJPARepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepository {

  private final KeywordJPARepository keywordJPARepository;
  private final KeywordRepositoryCustom keywordRepositoryCustom;

  @Override
  public List<Keyword> saveAll(List<Keyword> keywords) {
    return keywordJPARepository.saveAll(keywords);
  }

  @Override
  public Optional<Keyword> findById(Long keywordId) {
    return Optional.empty();
  }

  @Override
  public List<Keyword> findAllByContentContaining(String content) {
    return keywordRepositoryCustom.findAllByContentContaining(content);
  }

  @Override
  public List<Keyword> findAll() {
    return keywordJPARepository.findAll();
  }
}
