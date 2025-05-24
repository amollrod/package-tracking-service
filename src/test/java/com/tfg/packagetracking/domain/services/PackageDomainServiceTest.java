package com.tfg.packagetracking.domain.services;

import com.tfg.packagetracking.domain.exceptions.PackageNotFoundException;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.domain.ports.BlockchainServicePort;
import com.tfg.packagetracking.domain.ports.PackageEventPublisherPort;
import com.tfg.packagetracking.domain.ports.PackageRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageDomainServiceTest {

    private PackageRepositoryPort repository;
    private PackageEventPublisherPort eventPublisher;
    private BlockchainServicePort blockchainService;
    private PackageDomainService service;

    @BeforeEach
    void setUp() {
        repository = mock(PackageRepositoryPort.class);
        eventPublisher = mock(PackageEventPublisherPort.class);
        blockchainService = mock(BlockchainServicePort.class);
        service = new PackageDomainService(repository, eventPublisher, blockchainService);
    }

    @Test
    void shouldReturnPackage_whenExists() {
        Package pkg = Package.create("Madrid", "Barcelona");
        when(repository.findById("id")).thenReturn(Optional.of(pkg));

        Optional<Package> result = service.getPackageById("id");

        assertThat(result).contains(pkg);
    }

    @Test
    void shouldReturnEmpty_whenPackageDoesNotExist() {
        when(repository.findById("id")).thenReturn(Optional.empty());

        Optional<Package> result = service.getPackageById("id");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnPackageHistory_whenPackageExists() {
        Package pkg = Package.create("Madrid", "Valencia");
        when(repository.findById("123")).thenReturn(Optional.of(pkg));
        when(blockchainService.getPackageHistory("123")).thenReturn(List.of(
                new PackageHistoryEvent("CREATED", "Madrid", Instant.now().getEpochSecond())
        ));

        List<PackageHistoryEvent> history = service.getPackageHistory("123");

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getStatus()).isEqualTo("CREATED");
    }

    @Test
    void shouldThrow_whenGettingHistoryForNonexistentPackage() {
        when(repository.findById("not-found")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPackageHistory("not-found"))
                .isInstanceOf(PackageNotFoundException.class);
    }

    @Test
    void shouldFilterPackages() {
        Page<Package> page = new PageImpl<>(List.of(Package.create("A", "B")));
        when(repository.findByFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<Package> result = service.findByFilters(
                PackageStatus.CREATED, "A", "B", "C", Instant.now(), Instant.now(), Pageable.unpaged()
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOrigin()).isEqualTo("A");
        assertThat(result.getContent().get(0).getDestination()).isEqualTo("B");
    }

    @Test
    void shouldCreatePackageAndPublishEvent() {
        Package created = service.createPackage("Sevilla", "Bilbao");

        verify(repository).save(any(Package.class));
        verify(eventPublisher).publishPackageCreatedEvent(created);
        assertThat(created.getOrigin()).isEqualTo("Sevilla");
    }

    @Test
    void shouldUpdatePackageStatusAndPublishEvent() {
        Package existing = Package.create("X", "Y");
        when(repository.findById("id")).thenReturn(Optional.of(existing));

        Package updated = service.updatePackageStatus("id", PackageStatus.IN_TRANSIT, "Zaragoza");

        verify(repository).save(updated);
        verify(eventPublisher).publishPackageUpdatedEvent(updated);
        assertThat(updated.getStatus()).isEqualTo(PackageStatus.IN_TRANSIT);
    }

    @Test
    void shouldThrow_whenUpdatingNonexistentPackage() {
        when(repository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePackageStatus("bad-id", PackageStatus.DELIVERED, "Z"))
                .isInstanceOf(PackageNotFoundException.class);
    }
}
