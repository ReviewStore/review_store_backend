package com.retro.domain.retro.presentation;

import com.retro.domain.retro.application.RetroService;
import com.retro.domain.retro.application.dto.RetroCursorPageResponse;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.KeywordResponse;
import com.retro.domain.retro.application.dto.response.RetroCreateResponse;
import com.retro.domain.retro.application.dto.response.RetroDetailResponse;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/retros")
public class RetroController {

  private final RetroService retroService;
  private final SecurityUtil securityUtil;

  @PostMapping
  public ApiResponse<RetroCreateResponse> createRetro(
      @RequestBody @Valid RetroCreateRequest request) {
    Retro retro = retroService.createRetro(securityUtil.getAuthenticatedUserId(), request);
    RetroCreateResponse resonse = RetroCreateResponse.of(retro);
    return ApiResponse.success(resonse);
  }

  @GetMapping("/keyword-search")
  public ApiResponse<List<KeywordResponse>> search(@RequestParam String content) {
    List<KeywordResponse> responses = retroService.searchKeywords(content);
    return ApiResponse.success(responses);
  }

  @GetMapping("/{retroId}")
  public ApiResponse<RetroDetailResponse> getRetro(@PathVariable Long retroId) {
    Long viewerId = securityUtil.getAuthenticatedUserId();
    RetroDetailResponse response = retroService.getRetro(viewerId, retroId);
    return ApiResponse.success(response);
  }

  @GetMapping("/my-retros")
  public ApiResponse<RetroCursorPageResponse> getMyRetros(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "20") int size) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    RetroCursorPageResponse response = retroService.getMyRetros(memberId, cursorId, size);
    return ApiResponse.success(response);
  }
}

