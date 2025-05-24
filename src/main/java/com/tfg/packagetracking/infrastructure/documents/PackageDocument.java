package com.tfg.packagetracking.infrastructure.documents;

import com.tfg.packagetracking.domain.models.PackageStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDocument {
    @Id
    private String id;
    private String origin;
    private String destination;
    private PackageStatus status;
    private List<PackageHistoryEventDocument> history;
}
