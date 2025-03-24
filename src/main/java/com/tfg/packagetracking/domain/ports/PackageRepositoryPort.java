package com.tfg.packagetracking.domain.ports;

import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface PackageRepositoryPort {
    Optional<Package> findById(String id);
    Page<Package> findByFilters(PackageStatus status, String origin, String destination, String location, Instant fromDate, Instant toDate, Pageable pageable);
    void save(Package pkg);
}
