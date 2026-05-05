package com.retro.domain.block.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.retro.domain.block.infrastructure.jpa.BlockJPARepository;
import com.retro.domain.block.domain.entity.Block;
import com.retro.global.common.exception.DuplicateBlockException;
import com.retro.global.common.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

  @Mock
  private BlockJPARepository blockRepository;

  @Mock
  private SecurityUtil securityUtil;

  @InjectMocks
  private BlockService blockService;

  @BeforeEach
  void setUp() {
    // MockitoAnnotations handled by @ExtendWith
  }

  @Test
  void blockMember_success_whenNotAlreadyBlocked() {
    Long requesterId = 1L;
    Long targetId = 2L;

    when(securityUtil.getAuthenticatedUserId()).thenReturn(requesterId);
    when(blockRepository.existsByBlockerMemberIdAndBlockedMemberId(requesterId, targetId))
        .thenReturn(false);

    assertDoesNotThrow(() -> blockService.blockMember(targetId));

    ArgumentCaptor<Block> captor = ArgumentCaptor.forClass(Block.class);
    verify(blockRepository).save(captor.capture());

    Block saved = captor.getValue();
    // use getters
    org.junit.jupiter.api.Assertions.assertEquals(requesterId, saved.getBlockerMemberId());
    org.junit.jupiter.api.Assertions.assertEquals(targetId, saved.getBlockedMemberId());
  }

  @Test
  void blockMember_throwDuplicate_whenAlreadyBlocked() {
    Long requesterId = 1L;
    Long targetId = 2L;

    when(securityUtil.getAuthenticatedUserId()).thenReturn(requesterId);
    when(blockRepository.existsByBlockerMemberIdAndBlockedMemberId(requesterId, targetId))
        .thenReturn(true);

    assertThrows(DuplicateBlockException.class, () -> blockService.blockMember(targetId));

    verify(blockRepository, never()).save(any(Block.class));
  }

  @Test
  void blockMember_ignore_whenSelfBlock() {
    Long requesterId = 1L;
    Long targetId = 1L;

    when(securityUtil.getAuthenticatedUserId()).thenReturn(requesterId);

    assertDoesNotThrow(() -> blockService.blockMember(targetId));

    verify(blockRepository, never()).existsByBlockerMemberIdAndBlockedMemberId(any(), any());
    verify(blockRepository, never()).save(any(Block.class));
  }
}

