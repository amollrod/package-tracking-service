package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.application.ports.PackageEventPublisherPort;
import com.tfg.packagetracking.application.mappers.PackageMapper;
import com.tfg.packagetracking.domain.models.Package;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageEventKafkaAdapter implements PackageEventPublisherPort {
    private final KafkaTemplate<String, PackageResponse> kafkaTemplate;

    public PackageEventKafkaAdapter(KafkaTemplate<String, PackageResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPackageUpdatedEvent(Package packageEntity) {
        PackageResponse packageResponse = PackageMapper.toResponse(packageEntity);
        kafkaTemplate.send("package-updates", packageResponse);
    }
}
