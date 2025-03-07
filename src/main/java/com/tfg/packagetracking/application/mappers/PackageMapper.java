package com.tfg.packagetracking.application.mappers;

import com.tfg.packagetracking.application.dto.PackageHistoryResponse;
import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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
                .lastLocation(packageEntity.getLastLocation())
                .lastTimestamp(packageEntity.getLastTimestamp())
                .history(toHistoryResponse(packageEntity.getHistory()))
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

    /**
     * Converts a PackageHistoryEvent entity to a PackageHistoryResponse DTO.
     *
     * @param historyEvent The PackageHistoryEvent entity to convert.
     * @return The PackageHistoryResponse DTO.
     */
    public static PackageHistoryResponse toHistoryResponse(PackageHistoryEvent historyEvent) {
        return PackageHistoryResponse.builder()
                .status(historyEvent.getStatus())
                .location(historyEvent.getLocation())
                .timestamp(historyEvent.getTimestamp())
                .build();
    }

    /**
     * Converts a list of PackageHistoryEvent entities to a list of PackageHistoryResponse DTOs.
     *
     * @param historyEvents The list of PackageHistoryEvent entities to convert.
     * @return The list of PackageHistoryResponse DTOs.
     * @see PackageMapper#toHistoryResponse(PackageHistoryEvent)
     */
    public static List<PackageHistoryResponse> toHistoryResponse(List<PackageHistoryEvent> historyEvents) {
        return historyEvents == null ? List.of() :
                historyEvents.stream()
                        .map(PackageMapper::toHistoryResponse)
                        .collect(Collectors.toList());
    }
}
