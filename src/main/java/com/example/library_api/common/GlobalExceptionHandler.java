package com.example.library_api.common;

import com.example.library_api.book.NotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String,Object>> notFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","NOT_FOUND","message",ex.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
    Map<String,String> fields = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("error","VALIDATION_ERROR","fields",fields));
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }
}
