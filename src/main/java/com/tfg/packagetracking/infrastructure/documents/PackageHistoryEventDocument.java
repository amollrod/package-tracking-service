package com.tfg.packagetracking.infrastructure.documents;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageHistoryEventDocument {
    private String status;
    private String location;
    private Long timestamp;
}
