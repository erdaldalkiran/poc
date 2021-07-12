package com.example.demo.infra.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Result {
    private Long id;

    private Long readyCount;

    private Long loadedCount;

    private Long unloadedCount;

    public Result merge(Result other) {
        readyCount += other.getReadyCount();
        loadedCount += other.getLoadedCount();
        unloadedCount += other.getUnloadedCount();

        return this;
    }

}
