package com.retro.domain.member.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.PostReadPermission;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Test
  @DisplayName("성공: 공개 설정을 true로 변경하면 게시물이 공개된다.")
  void updatePostPublicStatusOpen() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.APPLE, "apple-123", "닉네임", Term.from(true));
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    memberService.updatePostPublicStatus(memberId, true);

    // then
    assertThat(member.getIsPublic()).isTrue();
  }

  @Test
  @DisplayName("성공: 공개 설정을 false로 변경하면 게시물이 비공개가 되고 권한이 제한된다.")
  void updatePostPublicStatusClose() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.GOOGLE, "google-123", "닉네임", Term.from(true));
    member.openOwnPublication();
    member.grantPostReadPermission();
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    memberService.updatePostPublicStatus(memberId, false);

    // then
    assertThat(member.getIsPublic()).isFalse();
    assertThat(member.getPostReadPermission()).isEqualTo(PostReadPermission.LIMITED);
  }

  @Test
  @DisplayName("성공: 닉네임 변경 요청 시 닉네임이 업데이트된다.")
  void updateNickname() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.GOOGLE, "google-123", "기존닉네임", Term.from(true));
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    memberService.updateNickname(memberId, "새닉네임1");

    // then
    assertThat(member.getNickname()).isEqualTo("새닉네임1");
  }
}