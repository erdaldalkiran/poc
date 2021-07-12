package com.example.demo.infra.db;

import com.example.demo.domain.Count;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountRepository extends JpaRepository<Count, Long> {
    List<Count> findByIdIn(List<Long> ids);
}
