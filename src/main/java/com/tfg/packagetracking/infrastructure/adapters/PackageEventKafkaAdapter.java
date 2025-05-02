package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.ports.PackageEventPublisherPort;
import com.tfg.packagetracking.application.mappers.PackageMapper;
import com.tfg.packagetracking.domain.models.Package;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageEventKafkaAdapter implements PackageEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PackageEventKafkaAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a package event to a Kafka topic.
     *
     * @param topic         The topic to publish the event to.
     * @param packageEntity The package entity to publish.
     */
    private void publishEvent(String topic, Package packageEntity) {
        kafkaTemplate.send(topic, packageEntity.getId(), PackageMapper.toResponse(packageEntity));
    }

    @Override
    public void publishPackageCreatedEvent(Package packageEntity) {
        publishEvent("package-created", packageEntity);
    }

    @Override
    public void publishPackageUpdatedEvent(Package packageEntity) {
        publishEvent("package-updated", packageEntity);
    }
}
