package com.tfg.packagetracking.domain.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PackageTest {

    @Test
    void shouldCreatePackageWithInitialStatusAndHistory() {
        Package pkg = Package.create("Madrid", "Barcelona");

        assertThat(pkg.getOrigin()).isEqualTo("Madrid");
        assertThat(pkg.getDestination()).isEqualTo("Barcelona");
        assertThat(pkg.getStatus()).isEqualTo(PackageStatus.CREATED);
        assertThat(pkg.getHistory()).hasSize(1);

        PackageHistoryEvent event = pkg.getHistory().get(0);
        assertThat(event.getStatus()).isEqualTo("CREATED");
        assertThat(event.getLocation()).isEqualTo("Madrid");
        assertThat(event.getTimestamp()).isPositive();
    }

    @Test
    void shouldUpdateStatusAndAddHistoryEvent() {
        Package pkg = Package.create("Madrid", "Barcelona");
        pkg.updateStatus(PackageStatus.IN_TRANSIT, "Valencia");

        assertThat(pkg.getStatus()).isEqualTo(PackageStatus.IN_TRANSIT);
        assertThat(pkg.getHistory()).hasSize(2);

        PackageHistoryEvent last = pkg.getHistory().get(1);
        assertThat(last.getStatus()).isEqualTo("IN_TRANSIT");
        assertThat(last.getLocation()).isEqualTo("Valencia");
        assertThat(last.getTimestamp()).isPositive();
    }

    @Test
    void shouldReturnLastLocationAndTimestampCorrectly() throws InterruptedException {
        Package pkg = Package.create("Madrid", "Barcelona");
        long ts1 = pkg.getLastTimestamp();
        // Simulate some time passing to ensure timestamps are different
        Thread.sleep(1000);
        pkg.updateStatus(PackageStatus.IN_TRANSIT, "Zaragoza");
        long ts2 = pkg.getLastTimestamp();

        assertThat(ts2).isGreaterThanOrEqualTo(ts1);
        assertThat(pkg.getLastLocation()).isEqualTo("Zaragoza");
    }

    @Test
    void shouldHandleEmptyHistoryInGetters() {
        Package pkg = Package.builder()
                .origin("Madrid")
                .destination("Barcelona")
                .status(PackageStatus.CREATED)
                .history(List.of())
                .build();

        assertThat(pkg.getLastLocation()).isEqualTo("Madrid");
        assertThat(pkg.getLastTimestamp()).isEqualTo(0L);
    }
}
