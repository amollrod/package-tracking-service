package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.exceptions.BlockchainException;
import com.tfg.packagetracking.domain.exceptions.InvalidBlockchainResponseException;
import com.tfg.packagetracking.domain.exceptions.PackageHistoryNotFoundException;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class BlockchainServiceClientTest {

    private static final String BLOCKCHAIN_SERVICE_URL = "http://blockchain-service:3000";
    private BlockchainServiceClient client;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.client = new BlockchainServiceClient(restTemplate, BLOCKCHAIN_SERVICE_URL);
    }

    @Test
    void shouldReturnPackageHistory_whenValidResponse() {
        String json = "{\"history\": [{ \"status\": \"CREATED\", \"location\": \"Madrid\", \"timestamp\": 1700000000 }]}";

        mockServer.expect(requestTo(BLOCKCHAIN_SERVICE_URL + "/package/123/history"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PackageHistoryEvent> history = client.getPackageHistory("123");
        assertThat(history).hasSize(1);
        PackageHistoryEvent event = history.get(0);
        assertThat(event.getStatus()).isEqualTo("CREATED");
        assertThat(event.getLocation()).isEqualTo("Madrid");
        assertThat(event.getTimestamp()).isEqualTo(1700000000L);
    }

    @Test
    void shouldThrowInvalidBlockchainResponseException_whenMissingHistoryField() {
        String json = "{ \"foo\": [] }";

        mockServer.expect(requestTo(BLOCKCHAIN_SERVICE_URL + "/package/abc/history"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getPackageHistory("abc"))
                .isInstanceOf(InvalidBlockchainResponseException.class);
    }

    @Test
    void shouldThrowPackageHistoryNotFoundException_whenStatus404() {
        mockServer.expect(requestTo(BLOCKCHAIN_SERVICE_URL + "/package/missing/history"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getPackageHistory("missing"))
                .isInstanceOf(PackageHistoryNotFoundException.class);
    }

    @Test
    void shouldThrowBlockchainException_whenServerError() {
        mockServer.expect(requestTo(BLOCKCHAIN_SERVICE_URL + "/package/error/history"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getPackageHistory("error"))
                .isInstanceOf(BlockchainException.class);
    }
}
