package com.retro.domain.retro.domain.repository;

import com.retro.domain.retro.domain.entity.Retro;
import java.util.List;
import java.util.Optional;

public interface RetroRepository {

  Retro save(Retro retro);

  Optional<Retro> findById(Long retroId);

  List<Retro> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);

  void delete(Retro retro);
}
