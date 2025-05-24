package com.tfg.packagetracking.application.services;

import com.tfg.packagetracking.application.dto.CreatePackageRequest;
import com.tfg.packagetracking.application.dto.PackageHistoryResponse;
import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.domain.services.PackageDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PackageServiceTest {

    private PackageDomainService domainService;
    private PackageService packageService;

    @BeforeEach
    void setUp() {
        domainService = Mockito.mock(PackageDomainService.class);
        packageService = new PackageService(domainService);
    }

    @Test
    void shouldReturnPackageResponseWhenPackageExists() {
        Package pkg = Package.builder()
                .id("pkg-001")
                .origin("Madrid")
                .destination("Barcelona")
                .status(PackageStatus.CREATED)
                .history(List.of(PackageHistoryEvent.builder()
                        .status("CREATED")
                        .location("Madrid")
                        .timestamp(Instant.now().getEpochSecond())
                        .build()))
                .build();

        when(domainService.getPackageById("pkg-001")).thenReturn(Optional.of(pkg));

        Optional<PackageResponse> result = packageService.findPackage("pkg-001");

        assertThat(result).isPresent();
        assertThat(result.get().getOrigin()).isEqualTo("Madrid");
    }

    @Test
    void shouldReturnEmptyWhenPackageDoesNotExist() {
        when(domainService.getPackageById("not-found")).thenReturn(Optional.empty());

        Optional<PackageResponse> result = packageService.findPackage("not-found");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnHistoryResponseList() {
        List<PackageHistoryEvent> history = List.of(
                PackageHistoryEvent.builder().status("CREATED").location("Sevilla").timestamp(Instant.now().getEpochSecond()).build()
        );

        when(domainService.getPackageHistory("pkg-002")).thenReturn(history);

        List<PackageHistoryResponse> result = packageService.getPackageHistory("pkg-002");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("CREATED");
    }

    @Test
    void shouldReturnPaginatedResults() {
        PageRequest pageRequest = PageRequest.of(0, 2);

        List<Package> content = List.of(
                Package.builder().id("pkg-1").origin("A").destination("B").status(PackageStatus.CREATED)
                        .history(List.of(PackageHistoryEvent.builder().status("CREATED").location("A").timestamp(1L).build())).build(),
                Package.builder().id("pkg-2").origin("C").destination("D").status(PackageStatus.CREATED)
                        .history(List.of(PackageHistoryEvent.builder().status("CREATED").location("C").timestamp(2L).build())).build()
        );

        when(domainService.findByFilters(null, null, null, null, null, null, pageRequest))
                .thenReturn(new PageImpl<>(content, pageRequest, content.size()));

        Page<PackageResponse> page = packageService.findPackages(null, null, null, null, null, null, pageRequest);

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void shouldCreatePackage() {
        Package newPackage = Package.builder()
                .id("pkg-new")
                .origin("Madrid")
                .destination("Valencia")
                .status(PackageStatus.CREATED)
                .history(List.of(PackageHistoryEvent.builder().status("CREATED").location("Madrid").timestamp(1L).build()))
                .build();

        when(domainService.createPackage("Madrid", "Valencia")).thenReturn(newPackage);

        CreatePackageRequest request = CreatePackageRequest.builder()
                .origin("Madrid")
                .destination("Valencia")
                .build();

        PackageResponse response = packageService.createPackage(request);

        assertThat(response.getOrigin()).isEqualTo("Madrid");
        assertThat(response.getStatus()).isEqualTo(PackageStatus.CREATED);
    }

    @Test
    void shouldUpdatePackageStatus() {
        Package updatedPackage = Package.builder()
                .id("pkg-updated")
                .origin("Valencia")
                .destination("Bilbao")
                .status(PackageStatus.DELIVERED)
                .history(List.of(PackageHistoryEvent.builder().status("DELIVERED").location("Bilbao").timestamp(1L).build()))
                .build();

        when(domainService.updatePackageStatus("pkg-updated", PackageStatus.DELIVERED, "Bilbao"))
                .thenReturn(updatedPackage);

        PackageResponse response = packageService.updatePackageStatus("pkg-updated", PackageStatus.DELIVERED, "Bilbao");

        assertThat(response.getStatus()).isEqualTo(PackageStatus.DELIVERED);
    }
}
