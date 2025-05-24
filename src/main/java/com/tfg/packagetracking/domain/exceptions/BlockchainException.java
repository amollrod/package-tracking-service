package com.tfg.packagetracking.domain.exceptions;

/**
 * Base exception for blockchain-related issues.
 */
public class BlockchainException extends RuntimeException {
    public BlockchainException(String message) {
        super(message);
    }

    public BlockchainException(String message, Throwable cause) {
        super(message, cause);
    }
}
