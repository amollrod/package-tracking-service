package com.tfg.packagetracking.domain.exceptions;

/**
 * Exception thrown when the blockchain service returns an invalid or empty response.
 */
public class InvalidBlockchainResponseException extends BlockchainException {
    public InvalidBlockchainResponseException(String message) {
        super(message);
    }
}
