package com.tfg.packagetracking.domain.services;

import com.tfg.packagetracking.domain.ports.BlockchainServicePort;
import com.tfg.packagetracking.domain.ports.PackageEventPublisherPort;
import com.tfg.packagetracking.domain.ports.PackageRepositoryPort;
import com.tfg.packagetracking.domain.exceptions.PackageNotFoundException;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.models.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class PackageDomainService {
    private final PackageRepositoryPort repository;
    private final PackageEventPublisherPort eventPublisher;
    private final BlockchainServicePort blockchainService;

    public PackageDomainService(PackageRepositoryPort repository, PackageEventPublisherPort eventPublisher, BlockchainServicePort blockchainServicePort) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.blockchainService = blockchainServicePort;
    }

    public Optional<Package> getPackageById(String id) {
        return repository.findById(id);
    }

    public List<PackageHistoryEvent> getPackageHistory(String id) {
        if (repository.findById(id).isEmpty()) {
            throw new PackageNotFoundException(id);
        }
        return blockchainService.getPackageHistory(id);
    }

    public Page<Package> findByFilters(PackageStatus status, String origin, String destination, String location, Instant fromDate, Instant toDate, Pageable pageable) {
        return repository.findByFilters(status, origin, destination, location, fromDate, toDate, pageable);
    }

    public Package createPackage(String origin, String destination) {
        Package newPackage = Package.create(origin, destination);

        repository.save(newPackage);
        eventPublisher.publishPackageCreatedEvent(newPackage);

        return newPackage;
    }

    public Package updatePackageStatus(String id, PackageStatus status, String newLocation) {
        Package updatedPackage = repository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id))
                .updateStatus(status, newLocation);

        repository.save(updatedPackage);
        eventPublisher.publishPackageUpdatedEvent(updatedPackage);

        return updatedPackage;
    }
}
