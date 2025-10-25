package com.merendero.facil.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private String status;
    private String message;
    private String additionalInfo;

    public ErrorResponse(String status, String message, String additionalInfo) {
        this.status = status;
        this.message = message;
        this.additionalInfo = additionalInfo;
    }

    public ErrorResponse(String status, String message) {
        this(status, message, null);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
