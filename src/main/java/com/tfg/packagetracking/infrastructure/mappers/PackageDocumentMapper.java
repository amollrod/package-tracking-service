package com.tfg.packagetracking.infrastructure.mappers;

import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import com.tfg.packagetracking.infrastructure.documents.PackageHistoryEventDocument;

import java.util.stream.Collectors;

public class PackageDocumentMapper {

    public static PackageDocument toDocument(Package pkg) {
        return PackageDocument.builder()
                .id(pkg.getId())
                .origin(pkg.getOrigin())
                .destination(pkg.getDestination())
                .status(pkg.getStatus())
                .history(pkg.getHistory().stream().map(event ->
                        PackageHistoryEventDocument.builder()
                                .status(event.getStatus())
                                .location(event.getLocation())
                                .timestamp(event.getTimestamp())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    public static Package toDomain(PackageDocument doc) {
        return Package.builder()
                .id(doc.getId())
                .origin(doc.getOrigin())
                .destination(doc.getDestination())
                .status(doc.getStatus())
                .history(doc.getHistory().stream().map(event ->
                        new PackageHistoryEvent(
                                event.getStatus(),
                                event.getLocation(),
                                event.getTimestamp()
                        )
                ).collect(Collectors.toList()))
                .build();
    }
}
