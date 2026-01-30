package com.euandresimoes.spring_crm.shared.web;

public record ApiResponse<T>(
        int status,
        String error,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, null, data);
    }

    public static <T> ApiResponse<T> error(int status, String error) {
        return new ApiResponse<>(status, error, null);
    }
}
