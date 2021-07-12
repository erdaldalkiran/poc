package com.example.demo.infra.rest;

import com.example.demo.infra.kafka.consumer.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch-consumer/producer")
public class ProducerController {
    private final KafkaTemplate<String, Result> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topicName;

    @PostMapping("/produce/{count}")
    public ResponseEntity produce(@PathVariable Long count) {
        log.info("ProducerController.produce processing request with count: {}", count);

        for (long i = 0; i < count; i++) {

            //todo: generate random data
            var result = Result.builder()
                .id(i % 5)
                .readyCount(i)
                .unloadedCount(i)
                .loadedCount(i)
                .build();

            Message<Result> message = MessageBuilder
                .withPayload(result)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.MESSAGE_KEY, result.getId().toString())
                .build();

            kafkaTemplate.send(message);
        }

        return ResponseEntity.accepted().build();
    }
}
