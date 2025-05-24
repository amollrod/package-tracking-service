package com.tfg.packagetracking.infrastructure.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import com.tfg.packagetracking.infrastructure.repositories.MongoPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * This class is responsible for seeding the database with initial data when the application starts.
 * It creates packages if they do not already exist in the database.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final MongoPackageRepository packageRepository;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void seedPackagesIfNoneExist() {
        if (packageRepository.count() == 0) {
            try {
                InputStream inputStream = new ClassPathResource("packages.json").getInputStream();
                List<PackageDocument> packages = objectMapper.readValue(inputStream, new TypeReference<>() {});
                packageRepository.saveAll(packages);
                log.info("Se cargaron los paquetes iniciales.");
            } catch (Exception e) {
                log.error("Error al cargar los paquetes iniciales: {}", e.getMessage());
            }
        } else {
            log.info("Los paquetes ya existen. Seed ignorado.");
        }
    }
}
