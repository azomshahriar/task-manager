package com.cardinality.taskmanager.exception;

import com.cardinality.taskmanager.util.TaskManErrors;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Exception",ex);
        return new ErrorResponse(
                TaskManErrors.getErrorCode(TaskManErrors.TASK_SERVICE,TaskManErrors.INVALID_INPUT),
                errors.toString());

    }


    @ExceptionHandler({ElementNotFoundException.class})
    public final ResponseEntity<ErrorResponse> handleNotFoundExceptions(ElementNotFoundException ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    public final ResponseEntity<ErrorResponse> handleBadExceptions(BadRequestException ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({CommonException.class})
    public final ResponseEntity<ErrorResponse> handleCommonExceptions(CommonException ex) {
        log.error("Common Exception",ex);
        ErrorResponse error = new ErrorResponse(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({Exception.class,RuntimeException.class})
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(TaskManErrors.INTERNAL_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
