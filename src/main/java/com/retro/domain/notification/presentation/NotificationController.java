package com.retro.domain.notification.presentation;

import com.retro.domain.notification.application.NotificationService;
import com.retro.domain.notification.application.dto.NotificationCursorPageResponse;
import com.retro.domain.notification.application.dto.NotificationResponse;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification API", description = "알림 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final SecurityUtil securityUtil;

  @Operation(summary = "알림 단건 조회", description = "내 알림을 단건 조회합니다.")
  @GetMapping("/{notificationId}")
  public ApiResponse<NotificationResponse> getNotification(@PathVariable Long notificationId) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    NotificationResponse response = notificationService.getNotification(memberId, notificationId);
    return ApiResponse.success(response);
  }

  @Operation(summary = "알림 목록 조회", description = "커서 기반으로 내 알림 목록을 조회합니다.")
  @GetMapping
  public ApiResponse<NotificationCursorPageResponse> getNotifications(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "20") int size
  ) {
    Long memberId = securityUtil.getAuthenticatedUserId();
    NotificationCursorPageResponse response = notificationService.getNotifications(memberId,
        cursorId, size);
    return ApiResponse.success(response);
  }
}
