package com.retro.domain.notice.presentation;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retro.domain.notice.application.NoticeService;
import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.domain.entity.Notice;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("secret")
class NoticeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private NoticeService noticeService;


  @Test
  @DisplayName("관리자는 공지사항을 작성할 수 있다.")
  @WithMockUser(roles = "ADMIN")
  void createNoticeAsAdmin() throws Exception {
    NoticeCreateRequest request = new NoticeCreateRequest("공지 제목", "공지 내용");
    Notice notice = Notice.of("공지 제목", "공지 내용");
    ReflectionTestUtils.setField(notice, "noticeId", 1L);
    NoticeCreateResponse response = NoticeCreateResponse.of(notice.getNoticeId(),
        LocalDateTime.now());

    when(noticeService.createNotice(any(NoticeCreateRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/v1/notices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.noticeId").value(1L));
  }

  @Test
  @DisplayName("회원은 공지사항을 작성할 수 없다.")
  @WithMockUser(roles = "MEMBER")
  void createNoticeAsMemberForbidden() throws Exception {
    NoticeCreateRequest request = new NoticeCreateRequest("공지 제목", "공지 내용");

    mockMvc.perform(post("/api/v1/notices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());

  }

  @Test
  @DisplayName("회원은 공지사항을 조회할 수 있다.")
  @WithMockUser(roles = "MEMBER")
  void getNoticeAsMember() throws Exception {
    Notice notice = Notice.of("공지 제목", "공지 내용");
    ReflectionTestUtils.setField(notice, "noticeId", 1L);

    when(noticeService.getNotice(1L)).thenReturn(notice);

    mockMvc.perform(get("/api/v1/notices/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.noticeId").value(1L))
        .andExpect(jsonPath("$.data.title").value("공지 제목"))
        .andExpect(jsonPath("$.data.content").value("공지 내용"));
  }
}