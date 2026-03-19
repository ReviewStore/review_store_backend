package com.retro.domain.retro.presentation;

import com.retro.domain.retro.application.RetroService;
import com.retro.domain.retro.application.dto.RetroCursorPageResponse;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.request.RetroUpdateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroCreateResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Retro API", description = "회고 작성 및 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/retros")
public class RetroController {

  private final RetroService retroService;
  private final SecurityUtil securityUtil;

  @Operation(
      summary = "회고 작성",
      description = "회고를 작성하고 결과를 반환합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PostMapping
  public ApiResponse<RetroCreateResponse> createRetro(
      @RequestBody @Valid RetroCreateRequest request) {
    Retro retro = retroService.createRetro(securityUtil.getAuthenticatedUserId(), request);
    RetroCreateResponse resonse = RetroCreateResponse.of(retro);
    return ApiResponse.success(resonse);
  }

  @Operation(
      summary = "키워드 검색",
      description = "입력된 내용으로 키워드를 검색합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @GetMapping("/keyword-search")
  public ApiResponse<List<KeywordResponse>> search(@RequestParam String content) {
    List<KeywordResponse> responses = retroService.searchKeywords(content);
    return ApiResponse.success(responses);
  }

  @Operation(
      summary = "회고 상세 조회",
      description = "회고 ID로 상세 정보를 조회합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @GetMapping("/{retroId}")
  public ApiResponse<RetroDetailResponse> getRetro(@PathVariable Long retroId) {
    Long viewerId = securityUtil.getAuthenticatedUserId();
    RetroDetailResponse response = retroService.getRetro(viewerId, retroId);
    return ApiResponse.success(response);
  }

  @Operation(
      summary = "내 회고 목록 조회",
      description = "커서 기반으로 내 회고 목록을 조회합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @GetMapping("/my-retros")
  public ApiResponse<RetroCursorPageResponse> getMyRetros(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "20") int size) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    RetroCursorPageResponse response = retroService.getMyRetros(memberId, cursorId, size);
    return ApiResponse.success(response);
  }

  @Operation(
      summary = "회고 신고",
      description = "회고 신고를 누적하고 2회 이상이면 블라인드 처리합니다."
  )
  @PostMapping("/{retroId}/reports")
  public ApiResponse<Void> reportRetro(@PathVariable Long retroId) {
    Long reporterId = securityUtil.getAuthenticatedUserId();
    retroService.reportRetro(reporterId, retroId);
    return ApiResponse.success();
  }

  @Operation(
          summary = "회고 수정",
          description = "회고에 대한 내용을 업데이트 합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "성공",
          content = @io.swagger.v3.oas.annotations.media.Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE
          )
  )
  @PutMapping("/{retroId}")
  public ApiResponse<Void> updateRetro(@PathVariable Long retroId, @RequestBody RetroUpdateRequest request) {

    return ApiResponse.success();
  }
}

