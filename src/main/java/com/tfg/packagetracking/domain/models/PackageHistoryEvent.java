package com.tfg.packagetracking.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PackageHistoryEvent {
    private String status;
    private String location;
    private Long timestamp;
}
