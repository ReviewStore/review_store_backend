package com.retro.domain.retro.infrastructure.jpa;

import com.retro.domain.retro.domain.entity.Retro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroJPARepository extends JpaRepository<Retro, Long> {

  List<Retro> findAllByMemberId(Long memberId);
}
