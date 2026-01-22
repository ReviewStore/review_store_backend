package com.retro.domain.retro.domain.repository;

import com.retro.domain.retro.domain.entity.Keyword;
import java.util.List;
import java.util.Optional;

public interface KeywordRepository {

  List<Keyword> saveAll(List<Keyword> keywords);

  Optional<Keyword> findById(Long keywordId);

  List<Keyword> findAllByContentContaining(String content);

  List<Keyword> findAll();
}
