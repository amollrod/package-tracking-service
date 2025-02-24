package com.tfg.packagetracking.application.mappers;

import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.domain.models.Package;
import org.springframework.data.domain.Page;

/**
 * Mapper class to convert Package entities to PackageResponse DTOs.
 */
public class PackageMapper {
    /**
     * Converts a Package entity to a PackageResponse DTO.
     *
     * @param packageEntity The Package entity to convert.
     * @return The PackageResponse DTO.
     */
    public static PackageResponse toResponse(Package packageEntity) {
        return PackageResponse.builder()
                .id(packageEntity.getId())
                .origin(packageEntity.getOrigin())
                .destination(packageEntity.getDestination())
                .status(packageEntity.getStatus())
                .currentLocation(packageEntity.getCurrentLocation())
                .timestamp(packageEntity.getTimestamp())
                .build();
    }

    /**
     * Converts a Page of Package entities to a Page of PackageResponse DTOs.
     *
     * @param packagePage The Page of Package entities to convert.
     * @return The Page of PackageResponse DTOs.
     */
    public static Page<PackageResponse> toResponsePage(Page<Package> packagePage) {
        return packagePage.map(PackageMapper::toResponse);
    }
}
