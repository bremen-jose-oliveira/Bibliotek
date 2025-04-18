package com.bibliotek.personal.apiResponse;

public class ApiResponse {
    private String message;
    private boolean success;
    private int statusCode;

    public ApiResponse(String message, boolean success, int statusCode) {
        this.message = message;
        this.success = success;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
