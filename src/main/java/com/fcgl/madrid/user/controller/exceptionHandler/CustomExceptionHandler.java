package com.fcgl.madrid.user.controller.exceptionHandler;

import java.util.ArrayList;
import java.util.List;

import com.fcgl.madrid.user.payload.InternalStatus;
import com.fcgl.madrid.user.payload.StatusCode;
import com.fcgl.madrid.user.payload.response.LoginResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + " " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + " " + error.getDefaultMessage());
        }

        InternalStatus internalStatus = new InternalStatus(StatusCode.PARAM, HttpStatus.BAD_REQUEST, errors);
        LoginResponse loginResponse = new LoginResponse(internalStatus, null);
        return handleExceptionInternal(
                ex, loginResponse, headers, internalStatus.getHttpCode(), request);
    }
}
