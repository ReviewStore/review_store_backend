package com.retro.domain.member.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.PostReadPermission;
import com.retro.domain.member.domain.entity.Provider;
import com.retro.domain.member.domain.entity.Term;
import com.retro.domain.member.domain.event.MemberEventPublisher;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberEventPublisher memberEventPublisher;

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

  @Test
  @DisplayName("성공: 회원 탈퇴 시 회고 작성자를 삭제 사용자로 변경하고 회원을 삭제한다")
  void withdrawMember() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.GOOGLE, "google-123", "닉네임", Term.from(true));
    ReflectionTestUtils.setField(member, "id", memberId);
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
    doNothing().when(memberRepository).delete(any(Member.class));
    doNothing().when(memberEventPublisher).publishMemberWithdrawnEvent(any(Member.class));

    // when
    memberService.withdrawMember(memberId);

    // then
    verify(memberRepository).delete(member);
    verify(memberEventPublisher).publishMemberWithdrawnEvent(any(Member.class));
  }

  @Test
  @DisplayName("성공: 마케팅 약관 동의 변경 요청 시 약관 정보가 업데이트된다.")
  void updateMarketingAgreed() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.GOOGLE, "google-123", "닉네임", Term.from(true));
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    memberService.updateMarketingAgreed(memberId, false);

    // then
    assertThat(member.getTerm().isMarketingAgreed()).isFalse();
  }

  @Test
  @DisplayName("성공: 서비스 약관 동의 변경 요청 시 약관 정보가 업데이트된다.")
  void updateServiceTermAgreed() {
    // given
    Long memberId = 1L;
    Member member = Member.of(Provider.GOOGLE, "google-123", "닉네임", Term.from(true));
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    memberService.updateServiceTermAgreed(memberId, false);

    // then
    assertThat(member.getTerm().isServiceTermAgreed()).isFalse();
  }
}