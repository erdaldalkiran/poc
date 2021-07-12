package com.example.demo.infra.kafka.consumer;

import com.example.demo.domain.Count;
import com.example.demo.infra.db.CountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResultListener {

    private final CountRepository countRepository;

    @KafkaListener(
        topics = "${kafka.topic}",
        groupId = "${kafka.group-id}",
        containerFactory = "kafkaListenerContainerFactory")
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.REPEATABLE_READ)
    public void listener(@Payload List<Result> events, Acknowledgment acknowledgment) throws Exception {
        log.info("ResultListener.listener processing new events {} count:{} ack: {}", events, events.size(), acknowledgment);

        process(events, acknowledgment);

        fail();

        acknowledgment.acknowledge();
    }

//    @Transactional()
    public void process(List<Result> events, Acknowledgment acknowledgment) throws Exception {
        var merged = events.stream().collect(Collectors.toMap(Result::getId, Function.identity(), Result::merge));
        log.info("ResultListener.process merged result: {}", merged);

        var storedCounts = countRepository.findByIdIn(merged.keySet().stream().toList());
        log.info("ResultListener.process stored counts: {}", storedCounts);

        storedCounts.stream().forEach(count -> {
            var result = merged.get(count.getId());
            count.updateReadyCount(result.getReadyCount());
            count.updateLoadedCount(result.getLoadedCount());
            count.updateUnloadedCount(result.getUnloadedCount());
            merged.remove(count.getId());
        });

        merged.values().forEach(result -> {
            storedCounts.add(new Count(result.getId(), result.getReadyCount(), result.getLoadedCount(), result.getUnloadedCount()));
        });

        countRepository.saveAll(storedCounts);

//        fail();

//        acknowledgment.acknowledge();
    }

    private void fail() throws Exception {
        throw new SQLException("ciko was here!");
    }
}
