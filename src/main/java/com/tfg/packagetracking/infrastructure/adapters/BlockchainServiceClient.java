package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.ports.BlockchainServicePort;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BlockchainServiceClient implements BlockchainServicePort {
    private final RestTemplate restTemplate;

    @Value("${blockchain.service.url}")
    private String blockchainServiceUrl;

    public BlockchainServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<PackageHistoryEvent> getPackageHistory(String packageId) {
        String url = String.format("%s/package/%s/history", blockchainServiceUrl, packageId);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("history")) {
                throw new RuntimeException("No se pudo obtener el historial del paquete: respuesta vacía o inválida.");
            }

            List<Map<String, Object>> historyList = (List<Map<String, Object>>) response.getBody().get("history");

            return historyList.stream().map(event -> new PackageHistoryEvent(
                    event.get("status").toString(),
                    event.get("location").toString(),
                    Long.parseLong(event.get("timestamp").toString())
            )).collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Historial no encontrado para el paquete con ID: " + packageId);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al comunicarse con BlockchainService: " + e.getMessage(), e);
        }
    }
}
