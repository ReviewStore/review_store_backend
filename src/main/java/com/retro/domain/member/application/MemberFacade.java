package com.retro.domain.member.application;

import com.retro.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class MemberFacade {

  private final MemberService memberService;


  public Member getMember(Long memberId) {
    return memberService.getMember(memberId);
  }

  public void grantUnlimitedPostReadPermissionToMember(Member member) {
    memberService.grantUnlimitedPostReadPermissionToMember(member);
  }
}
