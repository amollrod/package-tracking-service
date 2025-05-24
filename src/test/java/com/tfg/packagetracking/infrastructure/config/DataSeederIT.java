package com.tfg.packagetracking.infrastructure.config;

import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import com.tfg.packagetracking.infrastructure.repositories.MongoPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DataSeeder using real MongoDB container and verifying initial data loads.
 */
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DataSeederIT {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MongoPackageRepository packageRepository;

    @BeforeEach
    void setUp() {
        assertThat(packageRepository).isNotNull();
    }

    @Test
    void shouldSeedInitialPackagesFromJsonFile() {
        List<PackageDocument> documents = packageRepository.findAll();
        assertThat(documents).isNotEmpty();
        assertThat(documents.size()).isEqualTo(5);
    }
}
