package com.tfg.packagetracking.domain.exceptions;

public class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(long id) {
        super("Package with ID " + id + " not found.");
    }
}
