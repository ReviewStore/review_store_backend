package com.retro.domain.notice.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.domain.entity.Notice;
import com.retro.domain.notice.domain.entity.repository.NoticeRepository;
import com.retro.global.common.exception.BusinessException;
import com.retro.global.common.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

  @Mock
  private NoticeRepository noticeRepository;

  @InjectMocks
  private NoticeService noticeService;

  @Test
  @DisplayName("공지사항 작성 시 저장된 공지사항을 반환한다.")
  void createNotice() {

    // given
    NoticeCreateRequest request = new NoticeCreateRequest("공지 제목", "공지 내용");
    Notice notice = Notice.of("공지 제목", "공지 내용");
    ReflectionTestUtils.setField(notice, "noticeId", 1L);

    when(noticeRepository.createNotice(any(Notice.class)))
        .thenReturn(notice);

    // when
    NoticeCreateResponse saved = noticeService.createNotice(request);

    // then
    assertThat(saved.noticeId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("공지사항 조회 시 존재하지 않으면 예외를 던진다.")
  void getNoticeNotFound() {
    // given
    Long notRegisteredNoticeId = 1L;

    // when & then
    when(noticeRepository.getNotice(anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noticeService.getNotice(notRegisteredNoticeId))
        .isInstanceOf(BusinessException.class)
        .hasMessage(ErrorCode.NOTICE_NOT_FOUND.getMessage());
  }
}