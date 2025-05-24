package com.tfg.packagetracking.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.packagetracking.application.dto.CreatePackageRequest;
import com.tfg.packagetracking.application.dto.PackageHistoryResponse;
import com.tfg.packagetracking.application.dto.PackageResponse;
import com.tfg.packagetracking.application.services.PackageService;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PackageController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class PackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PackageService packageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "CREATE_PACKAGE")
    void shouldCreatePackage() throws Exception {
        CreatePackageRequest request = CreatePackageRequest.builder()
                .origin("Madrid")
                .destination("Barcelona")
                .build();

        PackageResponse response = PackageResponse.builder()
                .id("pkg-123")
                .origin("Madrid")
                .destination("Barcelona")
                .status(PackageStatus.CREATED)
                .build();

        when(packageService.createPackage(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/packages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pkg-123"));
    }

    @Test
    @WithMockUser(authorities = "FIND_PACKAGE")
    void shouldReturnPackageById() throws Exception {
        PackageResponse response = PackageResponse.builder()
                .id("pkg-456")
                .origin("Sevilla")
                .destination("Granada")
                .status(PackageStatus.IN_TRANSIT)
                .build();

        when(packageService.findPackage("pkg-456")).thenReturn(Optional.of(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/packages/pkg-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Sevilla"));
    }

    @Test
    @WithMockUser(authorities = "FIND_PACKAGE")
    void shouldReturnNotFoundForMissingPackage() throws Exception {
        when(packageService.findPackage("missing")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/packages/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "VIEW_HISTORY")
    void shouldReturnPackageHistory() throws Exception {
        List<PackageHistoryResponse> history = List.of(
                PackageHistoryResponse.builder()
                        .status("CREATED")
                        .location("Madrid")
                        .timestamp(Instant.now().getEpochSecond())
                        .build()
        );

        when(packageService.getPackageHistory("pkg-789")).thenReturn(history);

        mockMvc.perform(MockMvcRequestBuilders.get("/packages/pkg-789/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }

    @Test
    @WithMockUser(authorities = "UPDATE_STATUS")
    void shouldUpdatePackageStatus() throws Exception {
        PackageResponse updated = PackageResponse.builder()
                .id("pkg-101")
                .origin("Valencia")
                .destination("Bilbao")
                .status(PackageStatus.DELIVERED)
                .build();

        when(packageService.updatePackageStatus("pkg-101", PackageStatus.DELIVERED, "Bilbao"))
                .thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.patch("/packages/pkg-101/status")
                        .with(csrf())
                        .param("status", "DELIVERED")
                        .param("newLocation", "Bilbao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/packages/pkg-123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "VIEW_HISTORY") // Missing authority for this endpoint
    void shouldReturnForbidden_whenLackingAuthority() throws Exception {
        CreatePackageRequest request = CreatePackageRequest.builder()
                .origin("Madrid")
                .destination("Barcelona")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/packages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CREATE_PACKAGE")
    void shouldReturnForbidden_whenMissingCsrfToken() throws Exception {
        CreatePackageRequest request = CreatePackageRequest.builder()
                .origin("Madrid")
                .destination("Barcelona")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "UPDATE_STATUS")
    void shouldReturnForbidden_whenPatchWithoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/packages/pkg-101/status")
                        .param("status", "DELIVERED")
                        .param("newLocation", "Bilbao"))
                .andExpect(status().isForbidden());
    }
}
