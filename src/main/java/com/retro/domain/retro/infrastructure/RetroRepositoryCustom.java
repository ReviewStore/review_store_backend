package com.retro.domain.retro.infrastructure;

import com.retro.domain.retro.domain.entity.Retro;
import java.util.List;

public interface RetroRepositoryCustom {

  List<Retro> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);
}
