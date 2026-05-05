package com.retro.domain.block.infrastructure.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.retro.domain.block.domain.entity.Block;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class BlockJPARepositoryTest {

  @Autowired
  private BlockJPARepository blockRepository;

  @Test
  void save_and_existsBy_shouldWork() {
    Block block = Block.of(1L, 2L);
    blockRepository.saveAndFlush(block);

    boolean exists = blockRepository.existsByBlockerMemberIdAndBlockedMemberId(1L, 2L);
    assertThat(exists).isTrue();
  }

  @Test
  void duplicate_save_shouldThrow_DataIntegrityViolation() {
    Block b1 = Block.of(10L, 20L);
    blockRepository.saveAndFlush(b1);

    Block b2 = Block.of(10L, 20L);
    assertThrows(DataIntegrityViolationException.class, () -> {
      blockRepository.saveAndFlush(b2);
    });
  }
}

