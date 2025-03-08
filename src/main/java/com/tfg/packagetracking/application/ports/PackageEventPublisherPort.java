package com.tfg.packagetracking.application.ports;

import com.tfg.packagetracking.domain.models.Package;

public interface PackageEventPublisherPort {
    void publishPackageCreatedEvent(Package packageEntity);
    void publishPackageUpdatedEvent(Package packageEntity);
}
