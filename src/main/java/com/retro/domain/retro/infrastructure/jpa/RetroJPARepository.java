package com.retro.domain.retro.infrastructure.jpa;

import com.retro.domain.retro.domain.entity.Retro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroJPARepository extends JpaRepository<Retro, Long> {

}
