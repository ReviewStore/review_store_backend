package com.retro.domain.member.infrastructure;

import java.util.List;

public interface MemberRepositoryCustom {

  List<Long> findMemberIdsWithCursor(Long cursorId, int size);
}
