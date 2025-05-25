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
    @NotBlank(message = "Origin must not be blank")
    private String origin;
    @NotBlank(message = "Destination must not be blank")
    private String destination;
}
