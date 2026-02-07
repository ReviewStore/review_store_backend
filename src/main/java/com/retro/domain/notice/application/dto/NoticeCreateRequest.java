package com.retro.domain.notice.application.dto;

import com.retro.domain.notice.domain.entity.Notice;
import jakarta.validation.constraints.NotBlank;

public record NoticeCreateRequest(
    @NotBlank(message = "공지사항 제목은 필수입니다.")
    String title,
    @NotBlank(message = "공지사항 내용은 필수입니다.")
    String content
) {

  public Notice toEntity() {
    return Notice.of(title, content);
  }
}
