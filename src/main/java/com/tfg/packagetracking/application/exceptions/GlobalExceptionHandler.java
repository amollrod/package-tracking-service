package com.tfg.packagetracking.application.exceptions;

import com.tfg.packagetracking.domain.exceptions.BlockchainException;
import com.tfg.packagetracking.domain.exceptions.InvalidBlockchainResponseException;
import com.tfg.packagetracking.domain.exceptions.PackageHistoryNotFoundException;
import com.tfg.packagetracking.domain.exceptions.PackageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<String> handlePackageNotFound(PackageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PackageHistoryNotFoundException.class)
    public ResponseEntity<String> handlePackageHistoryNotFound(PackageHistoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidBlockchainResponseException.class)
    public ResponseEntity<String> handleInvalidBlockchainResponse(InvalidBlockchainResponseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Invalid response from blockchain service: " + ex.getMessage());
    }

    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<String> handleBlockchainException(BlockchainException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Blockchain service error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
