package com.retro.domain.member.application;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  public Member getMember(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
  }

  @Transactional
  public void grantUnlimitedPostReadPermissionToMember(Member member) {
    member.grantPostReadPermission();
  }

  @Transactional
  public void updatePostPublicStatus(Long memberId, boolean isPublic) {
    Member member = getMember(memberId);
    if (isPublic) {
      member.openOwnPublication();
      return;
    }
    member.closeOwnPublication();
  }

  @Transactional
  public void updateNickname(Long memberId, String nickname) {
    Member member = getMember(memberId);
    member.updateNickname(nickname);
  }
}
