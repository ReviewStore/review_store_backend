package com.retro.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    // 성공 응답 (데이터 있음)
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = 200;
        response.message = "Success";
        response.data = data;
        return response;
    }

    // 성공 응답 (데이터 없음)
    public static <T> ApiResponse<T> success() {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = 200;
        response.message = "Success";
        return response;
    }

    // 성공 응답 (커스텀 메시지)
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = 200;
        response.message = message;
        response.data = data;
        return response;
    }
}
