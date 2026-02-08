package com.retro.domain.notice.presentation;

import com.retro.domain.notice.application.NoticeService;
import com.retro.domain.notice.application.dto.NoticeCreateRequest;
import com.retro.domain.notice.application.dto.NoticeCreateResponse;
import com.retro.domain.notice.application.dto.NoticeResponse;
import com.retro.domain.notice.domain.entity.Notice;
import com.retro.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice API", description = "공지사항 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {

  private final NoticeService noticeService;

  @Operation(
      summary = "공지사항 생성",
      description = "새로운 공지사항을 등록합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PostMapping
  public ApiResponse<NoticeCreateResponse> createNotice(
      @RequestBody @Valid NoticeCreateRequest request) {
    NoticeCreateResponse response = noticeService.createNotice(request);
    return ApiResponse.success(response);
  }

  @Operation(
      summary = "공지사항 상세 조회",
      description = "공지사항 ID로 상세 정보를 조회합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @GetMapping("/{noticeId}")
  public ApiResponse<NoticeResponse> getNotice(@PathVariable Long noticeId) {
    Notice notice = noticeService.getNotice(noticeId);
    return ApiResponse.success(NoticeResponse.from(notice));
  }
}