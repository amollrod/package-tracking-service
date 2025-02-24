package com.tfg.packagetracking.application.ports;

import com.tfg.packagetracking.domain.models.Package;

public interface PackageEventPublisherPort {
    void publishPackageUpdatedEvent(Package packageEntity);
}
