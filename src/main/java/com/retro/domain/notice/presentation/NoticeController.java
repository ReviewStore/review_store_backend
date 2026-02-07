package com.retro.domain.notice.presentation;

import com.retro.domain.notice.application.NoticeService;
import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.application.dto.NoticeResponse;
import com.retro.domain.notice.domain.entity.Notice;
import com.retro.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {

  private final NoticeService noticeService;

  @PostMapping
  public ApiResponse<NoticeCreateResponse> createNotice(
      @RequestBody @Valid NoticeCreateRequest request) {
    NoticeCreateResponse response = noticeService.createNotice(request);
    return ApiResponse.success(response);
  }

  @GetMapping("/{noticeId}")
  public ApiResponse<NoticeResponse> getNotice(@PathVariable Long noticeId) {
    Notice notice = noticeService.getNotice(noticeId);
    return ApiResponse.success(NoticeResponse.from(notice));
  }
}