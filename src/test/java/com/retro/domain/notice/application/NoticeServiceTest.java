package com.retro.domain.notice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.application.event.NoticeEventPublisher;
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
  @Mock
  private NoticeEventPublisher noticeEventPublisher;
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

  @Test
  @DisplayName("공지사항 목록 조회 - 페이지 크기 이하이면 hasNext=false")
  void getNotices_whenLessOrEqualSize_hasNextFalse() {
    // given
    int size = 2;
    Notice n1 = Notice.of("t1", "c1");
    Notice n2 = Notice.of("t2", "c2");
    ReflectionTestUtils.setField(n1, "noticeId", 10L);
    ReflectionTestUtils.setField(n2, "noticeId", 9L);

    when(noticeRepository.findNoticesWithCursor(null, size + 1))
        .thenReturn(java.util.List.of(n1, n2));

    // when
    var response = noticeService.getNotices(null, size);

    // then
    assertThat(response.notices()).hasSize(2);
    assertThat(response.hasNext()).isFalse();
    assertThat(response.nextCursor()).isNull();
  }

  @Test
  @DisplayName("공지사항 목록 조회 - 요청 사이즈보다 1개 더 있으면 hasNext=true, nextCursor는 마지막 요소 id")
  void getNotices_whenMoreThanSize_hasNextTrue() {
    // given
    int size = 2;
    Notice n1 = Notice.of("t1", "c1");
    Notice n2 = Notice.of("t2", "c2");
    Notice n3 = Notice.of("t3", "c3");
    ReflectionTestUtils.setField(n1, "noticeId", 30L);
    ReflectionTestUtils.setField(n2, "noticeId", 29L);
    ReflectionTestUtils.setField(n3, "noticeId", 28L);

    when(noticeRepository.findNoticesWithCursor(null, size + 1))
        .thenReturn(java.util.List.of(n1, n2, n3));

    // when
    var response = noticeService.getNotices(null, size);

    // then
    assertThat(response.notices()).hasSize(2);
    assertThat(response.hasNext()).isTrue();
    // nextCursor should be id of the last element in the sliced list (n2)
    assertThat(response.nextCursor()).isEqualTo(29L);
  }
}