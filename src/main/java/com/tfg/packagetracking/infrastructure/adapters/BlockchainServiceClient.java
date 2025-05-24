package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.exceptions.BlockchainException;
import com.tfg.packagetracking.domain.exceptions.InvalidBlockchainResponseException;
import com.tfg.packagetracking.domain.exceptions.PackageHistoryNotFoundException;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.ports.BlockchainServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BlockchainServiceClient implements BlockchainServicePort {

    private final RestTemplate restTemplate;
    private final String blockchainServiceUrl;

    public BlockchainServiceClient(
            RestTemplate restTemplate,
            @Value("${blockchain.service.url}") String blockchainServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.blockchainServiceUrl = blockchainServiceUrl;
    }

    @Override
    public List<PackageHistoryEvent> getPackageHistory(String packageId) {
        String url = String.format("%s/package/%s/history", blockchainServiceUrl, packageId);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("history")) {
                throw new InvalidBlockchainResponseException("Empty or malformed response from blockchain service");
            }

            List<Map<String, Object>> historyList = (List<Map<String, Object>>) response.getBody().get("history");

            return historyList.stream()
                    .map(event -> new PackageHistoryEvent(
                            event.get("status").toString(),
                            event.get("location").toString(),
                            Long.parseLong(event.get("timestamp").toString())
                    ))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new PackageHistoryNotFoundException(packageId);
        } catch (RestClientException e) {
            throw new BlockchainException("Error communicating with blockchain service", e);
        }
    }
}
