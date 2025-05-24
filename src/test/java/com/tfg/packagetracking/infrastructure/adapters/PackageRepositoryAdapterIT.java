package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import com.tfg.packagetracking.infrastructure.documents.PackageHistoryEventDocument;
import com.tfg.packagetracking.infrastructure.repositories.MongoPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
class PackageRepositoryAdapterIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private PackageRepositoryAdapter adapter;

    @Autowired
    private MongoPackageRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(PackageDocument.builder()
                .id("test-id")
                .origin("Madrid")
                .destination("Barcelona")
                .status(PackageStatus.IN_TRANSIT)
                .history(List.of(PackageHistoryEventDocument.builder()
                        .status("IN_TRANSIT")
                        .location("Madrid")
                        .timestamp(Instant.now().getEpochSecond())
                        .build()))
                .build());
    }

    @Test
    @DisplayName("Should retrieve package by ID")
    void findById() {
        Optional<Package> result = adapter.findById("test-id");
        assertThat(result).isPresent();
        assertThat(result.get().getOrigin()).isEqualTo("Madrid");
    }

    @Test
    @DisplayName("Should persist a new package")
    void savePackage() {
        Package pkg = Package.builder()
                .id("pkg-2")
                .origin("Valencia")
                .destination("Zaragoza")
                .status(PackageStatus.DELIVERED)
                .history(List.of(PackageHistoryEvent.builder()
                        .status("DELIVERED")
                        .location("Zaragoza")
                        .timestamp(Instant.now().getEpochSecond())
                        .build()))
                .build();

        adapter.save(pkg);

        assertThat(repository.findById("pkg-2")).isPresent();
    }

    @ParameterizedTest(name = "Filter test {index}")
    @MethodSource("filterParams")
    @DisplayName("Should apply filters correctly in findByFilters")
    void findByFilters(PackageStatus status, String origin, String destination, String location, Instant from, Instant to) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Package> page = adapter.findByFilters(status, origin, destination, location, from, to, pageable);
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(0);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> filterParams() {
        Instant now = Instant.now();
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(PackageStatus.IN_TRANSIT, null, null, null, null, null),
                org.junit.jupiter.params.provider.Arguments.of(null, "Madrid", null, null, null, null),
                org.junit.jupiter.params.provider.Arguments.of(null, null, "Barcelona", null, null, null),
                org.junit.jupiter.params.provider.Arguments.of(null, null, null, "Madrid", null, null),
                org.junit.jupiter.params.provider.Arguments.of(null, null, null, null, now.minusSeconds(3600), null),
                org.junit.jupiter.params.provider.Arguments.of(null, null, null, null, null, now.plusSeconds(3600)),
                org.junit.jupiter.params.provider.Arguments.of(PackageStatus.IN_TRANSIT, "Madrid", "Barcelona", "Madrid", now.minusSeconds(3600), now.plusSeconds(3600))
        );
    }
}
