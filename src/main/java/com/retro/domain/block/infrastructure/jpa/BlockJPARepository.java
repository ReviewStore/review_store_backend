package com.retro.domain.block.infrastructure.jpa;

import com.retro.domain.block.domain.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockJPARepository extends JpaRepository<Block, Long> {
  boolean existsByBlockerMemberIdAndBlockedMemberId(Long blockerMemberId, Long blockedMemberId);
}


