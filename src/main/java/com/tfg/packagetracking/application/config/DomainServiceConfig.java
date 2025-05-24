package com.tfg.packagetracking.application.config;

import com.tfg.packagetracking.domain.ports.BlockchainServicePort;
import com.tfg.packagetracking.domain.ports.PackageEventPublisherPort;
import com.tfg.packagetracking.domain.ports.PackageRepositoryPort;
import com.tfg.packagetracking.domain.services.PackageDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides beans for domain services to keep domain layer framework-agnostic.
 */
@Configuration
public class DomainServiceConfig {
    /**
     * Creates a PackageDomainService bean with the necessary ports.
     *
     * @param repositoryPort the package repository port
     * @param eventPublisherPort the package event publisher port
     * @param blockchainServicePort the blockchain service port
     *
     * @return a configured PackageDomainService instance
     */
    @Bean
    public PackageDomainService packageDomainService(
            PackageRepositoryPort repositoryPort,
            PackageEventPublisherPort eventPublisherPort,
            BlockchainServicePort blockchainServicePort) {
        return new PackageDomainService(repositoryPort, eventPublisherPort, blockchainServicePort);
    }
}
