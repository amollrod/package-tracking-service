package com.tfg.packagetracking.application.dto;

import com.tfg.packagetracking.domain.models.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PackageResponse {
    private long id;
    private String origin;
    private String destination;
    private PackageStatus status;
    private String lastLocation;
    private long lastTimestamp;
    private List<PackageHistoryResponse> history;
}
