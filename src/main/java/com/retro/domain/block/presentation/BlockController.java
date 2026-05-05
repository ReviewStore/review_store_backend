package com.retro.domain.block.presentation;

import com.retro.domain.block.application.BlockService;
import com.retro.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/blocks")
@RestController
@RequiredArgsConstructor
public class BlockController {

  private final BlockService blockService;

  @Operation(
      summary = "회원 차단",
      description = "현재 인증 사용자가 {memberId}를 차단합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PostMapping("members/{memberId}")
  public ApiResponse<Void> blockMember(@PathVariable Long memberId) {
    blockService.blockMember(memberId);
    return ApiResponse.success();
  }

}
