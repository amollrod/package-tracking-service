package com.tfg.packagetracking.application.services;

import com.tfg.packagetracking.application.dto.CreatePackageRequest;
import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.application.mappers.PackageMapper;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.domain.services.PackageDomainService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PackageService {
    private final PackageDomainService domainService;

    public PackageService(PackageDomainService domainService) {
        this.domainService = domainService;
    }

    public Optional<PackageResponse> findPackage(String id) {
        return domainService.getPackageById(id)
                .map(PackageMapper::toResponse);
    }

    public Page<PackageResponse> findPackages(PackageStatus status, String destination, String currentLocation, Instant fromDate, Instant toDate, Pageable pageable) {
        return domainService.findByFilters(status, destination, currentLocation, fromDate, toDate, pageable)
                .map(PackageMapper::toResponse);
    }

    public PackageResponse createPackage(CreatePackageRequest request) {
        Package createdPackage = domainService.createPackage(
                request.getOrigin(),
                request.getDestination()
        );
        return PackageMapper.toResponse(createdPackage);
    }

    public PackageResponse updatePackageStatus(String id, PackageStatus status, String newLocation) {
        Package updatedPackage = domainService.updatePackageStatus(id, status, newLocation);
        return PackageMapper.toResponse(updatedPackage);
    }
}
