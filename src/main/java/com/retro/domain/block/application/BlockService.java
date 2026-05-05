package com.retro.domain.block.application;

import com.retro.domain.block.domain.entity.Block;
import com.retro.domain.block.infrastructure.jpa.BlockJPARepository;
import com.retro.global.common.exception.DuplicateBlockException;
import com.retro.global.common.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {

  private final BlockJPARepository blockRepository;
  private final SecurityUtil securityUtil;

  @Transactional
  public void blockMember(Long targetMemberId) {
    Long requesterId = securityUtil.getAuthenticatedUserId();

    if (requesterId.equals(targetMemberId)) {
      return;
    }

    boolean exists = blockRepository
        .existsByBlockerMemberIdAndBlockedMemberId(requesterId, targetMemberId);

    if (exists) {
      throw new DuplicateBlockException();
    }

    Block block = Block.of(requesterId, targetMemberId);
    blockRepository.save(block);
  }
}


