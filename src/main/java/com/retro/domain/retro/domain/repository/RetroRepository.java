package com.retro.domain.retro.domain.repository;

import com.retro.domain.retro.domain.entity.Retro;
import java.util.Optional;

public interface RetroRepository {

  Retro save(Retro retro);

  Optional<Retro> findById(Long retroId);

  void delete(Retro retro);
}
