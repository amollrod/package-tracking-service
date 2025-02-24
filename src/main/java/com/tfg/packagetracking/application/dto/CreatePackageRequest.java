package com.tfg.packagetracking.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreatePackageRequest {
    @NotBlank
    private String origin;
    @NotBlank
    private String destination;
}
