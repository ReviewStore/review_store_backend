package com.retro.domain.retro.presentation;

import com.retro.domain.retro.application.RetroService;
import com.retro.domain.retro.application.dto.request.RetroCreateRequest;
import com.retro.domain.retro.application.dto.response.RetroCreateResponse;
import com.retro.domain.retro.domain.entity.Retro;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
