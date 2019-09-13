package com.fcgl.madrid.user.payload;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;

public class InternalStatus {
    public static final InternalStatus OK = new InternalStatus(StatusCode.OK, HttpStatus.OK, "ok");
    public static final InternalStatus MISSING_PARAM = new InternalStatus(StatusCode.PARAM, HttpStatus.BAD_REQUEST, "Missing Required Param");
    public static final InternalStatus NOT_FOUND = new InternalStatus(StatusCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Data Not Found");

    private int code;
    private HttpStatus httpCode;
    private List<String> messages;

    public InternalStatus() {

    }

    public InternalStatus(StatusCode statusCode, HttpStatus httpCode, String message) {
        this.code = statusCode.getCode();
        this.httpCode = httpCode;
        this.messages = new ArrayList<String>();
        this.messages.add(message);
    }

    public InternalStatus(StatusCode statusCode, HttpStatus httpCode, List<String> messages) {
        this.code = statusCode.getCode();
        this.httpCode = httpCode;
        this.messages = messages;
    }


    public int getCode() {
        return this.code;
    }

    public HttpStatus getHttpCode() {
        return this.httpCode;
    }

    public List<String> getMessages() {
        return this.messages;
    }

}
