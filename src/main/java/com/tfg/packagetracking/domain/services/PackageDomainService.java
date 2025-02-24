package com.tfg.packagetracking.domain.services;

import com.tfg.packagetracking.application.ports.PackageRepositoryPort;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PackageDomainService {
    private final PackageRepositoryPort repository;

    public PackageDomainService(PackageRepositoryPort repository) {
        this.repository = repository;
    }

    public Optional<Package> getPackageById(String id) {
        return repository.findById(id);
    }

    public Page<Package> findByFilters(PackageStatus status, String destination, String currentLocation, Instant fromDate, Instant toDate,  Pageable pageable) {
        return repository.findByFilters(status, destination, currentLocation, fromDate, toDate, pageable);
    }

    public Package createPackage(String origin, String destination) {
        Package newPackage = Package.builder()
                .origin(origin)
                .destination(destination)
                .status(PackageStatus.CREATED)
                .timestamp(Instant.now())
                .currentLocation(origin)
                .build();
        repository.save(newPackage);
        return newPackage;
    }

    public Package updatePackageStatus(String id, PackageStatus status, String newLocation) {
        Optional<Package> packageOpt = repository.findById(id);
        if (packageOpt.isEmpty()) {
            throw new RuntimeException("Package with ID " + id + " not found.");
        }
        Package updatedPackage = packageOpt.get().updateStatus(status, newLocation);
        repository.save(updatedPackage);
        return updatedPackage;
    }
}
