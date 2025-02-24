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

    /**
     * Creates a new package with the given origin and destination.
     * <ul>
     *     <li>The package is created with status CREATED</li>
     *     <li>The current location is set to the origin</li>
     *     <li>The timestamp is set to the current time</li>
     * </ul>
     *
     * @param origin      the origin of the package
     * @param destination the destination of the package
     * @return the created package
     */
    public static Package create(String origin, String destination) {
        return Package.builder()
                .origin(origin)
                .destination(destination)
                .status(PackageStatus.CREATED)
                .timestamp(Instant.now())
                .currentLocation(origin)
                .build();
    }

    /**
     * Updates the status and location of the package.
     *
     * @param newStatus    the new status of the package
     * @param newLocation  the new location of the package
     * @return the updated package
     */
    public Package updateStatus(PackageStatus newStatus, String newLocation) {
        this.status = newStatus;
        this.currentLocation = newLocation;
        this.timestamp = Instant.now();
        return this;
    }
}
