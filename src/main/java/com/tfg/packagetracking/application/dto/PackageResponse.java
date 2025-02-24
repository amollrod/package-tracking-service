package com.tfg.packagetracking.application.dto;

import com.tfg.packagetracking.domain.models.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PackageResponse {
    private String id;
    private String origin;
    private String destination;
    private PackageStatus status;
    private String currentLocation;
    private Instant timestamp;
}
