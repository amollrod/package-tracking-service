package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.application.ports.BlockchainServicePort;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BlockchainServiceClient implements BlockchainServicePort {
    private final RestTemplate restTemplate;

    public BlockchainServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<PackageHistoryEvent> getPackageHistory(String packageId) {
        String url = "http://localhost:3000/package/" + packageId + "/history";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("history")) {
            throw new RuntimeException("No se pudo obtener el historial del paquete");
        }

        List<Map<String, Object>> historyList = (List<Map<String, Object>>) response.getBody().get("history");

        return historyList.stream().map(event -> new PackageHistoryEvent(
                (String) event.get("status"),
                (String) event.get("location"),
                Long.parseLong(event.get("timestamp").toString())
        )).collect(Collectors.toList());
    }
}
