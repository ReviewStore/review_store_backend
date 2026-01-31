package com.retro.domain.retro.infrastructure;

import com.retro.domain.retro.domain.entity.Keyword;
import java.util.List;

public interface KeywordRepositoryCustom {

  List<Keyword> findAllByContentContaining(String content);

}
