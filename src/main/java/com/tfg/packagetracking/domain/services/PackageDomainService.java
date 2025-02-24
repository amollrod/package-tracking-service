package com.tfg.packagetracking.domain.services;

import com.tfg.packagetracking.application.ports.PackageRepositoryPort;
import com.tfg.packagetracking.domain.exceptions.PackageNotFoundException;
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
        Package newPackage = Package.create(origin, destination);
        repository.save(newPackage);
        return newPackage;
    }

    public Package updatePackageStatus(String id, PackageStatus status, String newLocation) {
        Package packageEntity = repository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));

        Package updatedPackage = packageEntity.updateStatus(status, newLocation);
        repository.save(updatedPackage);
        return updatedPackage;
    }
}
