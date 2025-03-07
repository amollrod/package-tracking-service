package com.tfg.packagetracking.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PackageHistoryResponse {
    private String status;
    private String location;
    private long timestamp;
}
