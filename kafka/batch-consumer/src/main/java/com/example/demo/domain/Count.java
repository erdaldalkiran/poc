package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "count")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Count {
    @Id
    private Long id;

    private Long readyCount;

    private Long loadedCount;

    private Long unloadedCount;

    public void updateReadyCount(Long value){
        readyCount += value;
    }

    public void updateLoadedCount(Long value){
        loadedCount += value;
    }

    public void updateUnloadedCount(Long value){
        unloadedCount += value;
    }
}
