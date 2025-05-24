package com.tfg.packagetracking.domain.exceptions;

/**
 * Exception thrown when a package's history is not found in the blockchain service.
 */
public class PackageHistoryNotFoundException extends BlockchainException {
    public PackageHistoryNotFoundException(String packageId) {
        super("Package history not found for ID: " + packageId);
    }
}
