package com.retro.domain.block.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_blocks",
    uniqueConstraints = {@jakarta.persistence.UniqueConstraint(columnNames = {"blocker_member_id", "blocked_member_id"})})
public class Block {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long blockId;

  @Column(name = "blocker_member_id", nullable = false)
  private Long blockerMemberId;

  @Column(name = "blocked_member_id", nullable = false)
  private Long blockedMemberId;

  @Builder(access = AccessLevel.PRIVATE)
  private Block(Long blockerMemberId, Long blockedMemberId) {
    this.blockerMemberId = blockerMemberId;
    this.blockedMemberId = blockedMemberId;
  }

  public static Block of(Long blockerMemberId, Long blockedMemberId) {
    return Block.builder()
        .blockerMemberId(blockerMemberId)
        .blockedMemberId(blockedMemberId)
        .build();
  }
}

