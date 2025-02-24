package com.tfg.packagetracking.application.mappers;

import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.domain.models.Package;

public class PackageMapper {
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
}
