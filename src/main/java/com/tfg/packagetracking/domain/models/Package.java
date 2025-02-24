package com.tfg.packagetracking.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(collection = "packages")
public class Package {
    @Id
    private String id;
    private String origin;
    private String destination;
    private PackageStatus status;
    private Instant timestamp;
    private String currentLocation;

    public Package updateStatus(PackageStatus newStatus, String newLocation) {
        this.status = newStatus;
        this.currentLocation = newLocation;
        this.timestamp = Instant.now();
        return this;
    }
}
