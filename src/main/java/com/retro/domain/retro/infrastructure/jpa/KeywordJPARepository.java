package com.retro.domain.retro.infrastructure.jpa;

import com.retro.domain.retro.domain.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordJPARepository extends JpaRepository<Keyword, Long> {

}
