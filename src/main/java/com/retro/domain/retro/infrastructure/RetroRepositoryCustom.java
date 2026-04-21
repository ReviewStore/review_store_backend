package com.retro.domain.retro.infrastructure;

import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.entity.RetroReport;
import java.util.List;
import java.util.Optional;

public interface RetroRepositoryCustom {

  List<Retro> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);

  Optional<RetroReport> existsReportByRetroAndReporter(Long retroId, Long reporterMemberId);

  List<Retro> searchCommunityFeed(String keyword, String position, String interviewRound,
      Long cursorId, int size);

  List<Retro> findPublicRetrosWithCursor(Long cursorId, int size);

}
