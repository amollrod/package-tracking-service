package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import com.tfg.packagetracking.domain.models.PackageStatus;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class PackageEventKafkaAdapterIT {

    private static ConfluentKafkaContainer kafkaContainer;
    private static PackageEventKafkaAdapter adapter;

    @BeforeAll
    static void setUp() {
        kafkaContainer = new ConfluentKafkaContainer("confluentinc/cp-kafka:latest");
        kafkaContainer.start();

        var producerFactory = new DefaultKafkaProducerFactory<String, Object>(
                Map.of(
                        "bootstrap.servers", kafkaContainer.getBootstrapServers(),
                        "key.serializer", org.apache.kafka.common.serialization.StringSerializer.class,
                        "value.serializer", JsonSerializer.class,
                        JsonSerializer.ADD_TYPE_INFO_HEADERS, false
                )
        );
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setObservationEnabled(false); // evita errores en tests

        adapter = new PackageEventKafkaAdapter(kafkaTemplate);
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
    }

    private KafkaConsumer<String, String> createConsumer(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(topic));
        return consumer;
    }

    private Package samplePackage(String id) {
        return Package.builder()
                .id(id)
                .origin("Madrid")
                .destination("Sevilla")
                .status(PackageStatus.CREATED)
                .history(List.of(
                        PackageHistoryEvent.builder()
                                .status("CREATED")
                                .location("Madrid")
                                .timestamp(Instant.now().getEpochSecond())
                                .build()
                ))
                .build();
    }

    @Test
    void shouldPublishCreatedEventToKafka() throws Exception {
        KafkaConsumer<String, String> consumer = createConsumer("package-created");

        Package pkg = samplePackage("test-id-created");
        adapter.publishPackageCreatedEvent(pkg);

        ConsumerRecord<String, String> record = consumer.poll(Duration.ofSeconds(5)).iterator().next();

        assertThat(record.key()).isEqualTo("test-id-created");
        assertThat(record.value()).contains("\"origin\":\"Madrid\"");
        assertThat(record.topic()).isEqualTo("package-created");

        consumer.close();
    }

    @Test
    void shouldPublishUpdatedEventToKafka() throws Exception {
        KafkaConsumer<String, String> consumer = createConsumer("package-updated");

        Package pkg = samplePackage("test-id-updated");
        adapter.publishPackageUpdatedEvent(pkg);

        ConsumerRecord<String, String> record = consumer.poll(Duration.ofSeconds(5)).iterator().next();

        assertThat(record.key()).isEqualTo("test-id-updated");
        assertThat(record.value()).contains("\"destination\":\"Sevilla\"");
        assertThat(record.topic()).isEqualTo("package-updated");

        consumer.close();
    }
}
