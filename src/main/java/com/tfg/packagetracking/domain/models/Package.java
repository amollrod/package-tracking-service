package com.tfg.packagetracking.domain.models;

import com.tfg.packagetracking.domain.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(collection = "packages")
public class Package {
    @Id
    private long id;
    private String origin;
    private String destination;
    private PackageStatus status;
    private List<PackageHistoryEvent> history;

    /**
     * Creates a new package with the given origin and destination.
     * <ul>
     *     <li>The package is created with status CREATED</li>
     *     <li>A PackageHistoryEvent is added with the initial information</li>
     * </ul>
     *
     * @param origin      the origin of the package
     * @param destination the destination of the package
     * @return the created package
     */
    public static Package create(String origin, String destination) {
        long generatedId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        PackageStatus initialStatus = PackageStatus.CREATED;
        List<PackageHistoryEvent> initialHistory = List.of(
                PackageHistoryEvent.builder()
                        .status(initialStatus.name())
                        .location(origin)
                        .timestamp(TimeUtils.getCurrentTimestamp())
                        .build()
        );
        return Package.builder()
                .id(generatedId)
                .origin(origin)
                .destination(destination)
                .status(initialStatus)
                .history(initialHistory)
                .build();
    }

    /**
     * Updates the status and location of the package. It also adds a PackageHistoryEvent.
     *
     * @param newStatus   the new status of the package
     * @param newLocation the new location of the package
     * @return the updated package
     */
    public Package updateStatus(PackageStatus newStatus, String newLocation) {
        this.status = newStatus;

        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(
                PackageHistoryEvent.builder()
                        .status(newStatus.name())
                        .location(newLocation)
                        .timestamp(TimeUtils.getCurrentTimestamp())
                        .build()
        );

        return this;
    }

    /**
     * Returns the last event in the package history.
     *
     * @return the last event in the package history
     */
    private Optional<PackageHistoryEvent> getLastEvent() {
        return history.stream()
                .max(Comparator.comparingLong(PackageHistoryEvent::getTimestamp));
    }

    /**
     * Returns the location of the last event in the package history.
     *
     * @return the location of the last event in the package history
     */
    public String getLastLocation() {
        return getLastEvent().map(PackageHistoryEvent::getLocation).orElse(origin);
    }

    /**
     * Returns the timestamp of the last event in the package history.
     *
     * @return the timestamp of the last event in the package history
     */
    public long getLastTimestamp() {
        return getLastEvent().map(PackageHistoryEvent::getTimestamp).orElse(0L);
    }
}
