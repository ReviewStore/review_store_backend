package com.retro.domain.retro.domain.repository;

import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.entity.RetroReport;
import java.util.List;
import java.util.Optional;

public interface RetroRepository {

  Retro save(Retro retro);

  Optional<Retro> findById(Long retroId);

  List<Retro> findByMemberIdWithCursor(Long memberId, Long cursorId, int size);

  void delete(Retro retro);

  List<Retro> findAllByMemberId(Long memberId);

  Optional<RetroReport> existsReportByRetroAndReporter(Long retroId, Long reporterMemberId);

  List<Retro> searchCommunityFeed(String keyword, String position, String interviewRound,
      Long cursorId, int size);
}
