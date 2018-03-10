package com.rexwong.thinkingdata.client.rest;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author rexwong
 */
public class RestException extends RuntimeException {
    private HttpStatus httpStatus;

    public RestException(String msg) {
        super(msg);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException() {
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public Object getJson() throws IOException {
        Object response = JSON.parse(this.getMessage().getBytes("UTF-8"));
        return response;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
