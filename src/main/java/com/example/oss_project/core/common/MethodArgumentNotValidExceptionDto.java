package com.example.oss_project.core.common;

import com.example.oss_project.core.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MethodArgumentNotValidExceptionDto extends ExceptionDto {
    private final Map<String, String> errorFields;

    public MethodArgumentNotValidExceptionDto(final MethodArgumentNotValidException methodArgumentNotValidException) {
        super(ErrorCode.INVALID_REQUEST_BODY);
        this.errorFields = new HashMap<>();
        methodArgumentNotValidException.getBindingResult()
                .getAllErrors().forEach(e -> this.errorFields.put(((FieldError) e).getField(), e.getDefaultMessage()));
    }

    public MethodArgumentNotValidExceptionDto(final ConstraintViolationException constraintViolationException) {
        super(ErrorCode.INVALID_METHOD_ARGUMENT);
        this.errorFields = new HashMap<>();
        for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
            errorFields.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
    }
}
