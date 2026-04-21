package com.retro.domain.retro.presentation;

import com.retro.domain.retro.application.RetroService;
import com.retro.domain.retro.application.dto.RetroCursorPageResponse;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.CommunityRetroFeedCursorPageResponse;
import com.retro.domain.retro.application.dto.request.RetroUpdateRequest;
import com.retro.domain.retro.application.dto.response.CommunityRetroFeedCursorPageResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      description = "본인이 작성한 회고의 내용을 수정합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @PutMapping("/{retroId}")
  public ApiResponse<Void> updateRetro(@PathVariable Long retroId,
      @RequestBody @Valid RetroUpdateRequest request) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    retroService.updateRetro(memberId, retroId, request);
    return ApiResponse.success();
  }

  @Operation(
      summary = "회고 삭제",
      description = "본인이 작성한 회고를 삭제합니다."
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @io.swagger.v3.oas.annotations.media.Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE
      )
  )
  @DeleteMapping("/{retroId}")
  public ApiResponse<Void> deleteRetro(@PathVariable Long retroId) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    retroService.deleteRetro(memberId, retroId);
    return ApiResponse.success();
  }

  @Operation(
      summary = "커뮤니티 피드 조회",
      description = "공개 설정된 사용자의 익명 회고 카드 목록을 커서 기반으로 조회합니다."
  )
  @GetMapping("/community-feed")
  public ApiResponse<CommunityRetroFeedCursorPageResponse> getCommunityFeed(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "20") int size) {
    CommunityRetroFeedCursorPageResponse response = retroService.getCommunityFeed(cursorId, size);
    return ApiResponse.success(response);
  }

  @Operation(
      summary = "커뮤니티 피드 검색/필터링",
      description = "회사명·태그 키워드, 직무, 면접 차수로 공개 회고 카드를 검색합니다. 모든 조건은 선택 사항이며 커서 기반 페이지네이션을 지원합니다."
  )
  @GetMapping("/search-filters")
  public ApiResponse<CommunityRetroFeedCursorPageResponse> searchCommunityFeed(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String position,
      @RequestParam(required = false) String interviewRound,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "20") int size) {
    CommunityRetroFeedCursorPageResponse response = retroService.searchCommunityFeed(
        keyword, position, interviewRound, cursorId, size);
    return ApiResponse.success(response);
  }
}

