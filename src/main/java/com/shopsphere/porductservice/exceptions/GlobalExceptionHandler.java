package com.shopsphere.porductservice.exceptions;

import com.shopsphere.porductservice.dto.ErrorResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ResourceAlreadyExistException.class})
    public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExistException(final ResourceAlreadyExistException ex, final WebRequest request) {
        final ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.name())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final HashMap<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error ->
                errorMap.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errorMap);
    }
}
