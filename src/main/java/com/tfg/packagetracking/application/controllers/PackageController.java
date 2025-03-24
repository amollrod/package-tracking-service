package com.tfg.packagetracking.application.controllers;

import com.tfg.packagetracking.application.dto.CreatePackageRequest;
import com.tfg.packagetracking.application.dto.PackageHistoryResponse;
import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.application.services.PackageService;
import com.tfg.packagetracking.application.utils.ControllerUtils;
import com.tfg.packagetracking.domain.models.PackageStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping
    public ResponseEntity<PackageResponse> createPackage(@RequestBody @Valid CreatePackageRequest request) {
        return ResponseEntity.ok(packageService.createPackage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageResponse> findPackage(@PathVariable String id) {
        Optional<PackageResponse> packageResponse = packageService.findPackage(id);
        return packageResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PackageHistoryResponse>> getPackageHistory(@PathVariable String id) {
        List<PackageHistoryResponse> history = packageService.getPackageHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PackageResponse>> searchPackages(
            @RequestParam(required = false) PackageStatus status,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = ControllerUtils.createPageable(page, size);
        Page<PackageResponse> packages = packageService.findPackages(status, origin, destination, location, fromDate, toDate, pageable);

        return ControllerUtils.createPagedResponse(packages);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PackageResponse> updatePackageStatus(
            @PathVariable String id,
            @RequestParam PackageStatus status,
            @RequestParam String newLocation) {
        return ResponseEntity.ok(packageService.updatePackageStatus(id, status, newLocation));
    }
}
