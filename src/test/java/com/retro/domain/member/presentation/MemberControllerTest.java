package com.retro.domain.member.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retro.domain.member.application.MemberService;
import com.retro.domain.member.application.dto.MemberNicknameUpdateRequest;
import com.retro.global.common.utils.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("secret")
class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  // 외부 시스템이나 복잡한 서비스 로직은 MockitoBean으로 유지하여 테스트 격리
  @MockitoBean
  private MemberService memberService;

  @MockitoBean
  private SecurityUtil securityUtil;

  @Test
  @WithMockUser
  @DisplayName("통합 성공: 유효한 닉네임 변경 요청 시 200 OK")
  void updateNicknameSuccess() throws Exception {
    // Given
    Long memberId = 1L;
    MemberNicknameUpdateRequest request = new MemberNicknameUpdateRequest("회고러123");
    given(securityUtil.getAuthenticatedUserId()).willReturn(memberId);

    // When & Then
    mockMvc.perform(patch("/api/v1/members/nickname")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    then(memberService).should().updateNickname(eq(memberId), eq(request.nickname()));
  }

  @Test
  @WithMockUser
  @DisplayName("통합 실패: 유효하지 않은 닉네임 요청 시 400 BadRequest")
  void updateNicknameInvalid() throws Exception {
    // Given
    MemberNicknameUpdateRequest request = new MemberNicknameUpdateRequest("회고러!");

    // When & Then
    mockMvc.perform(patch("/api/v1/members/nickname")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}