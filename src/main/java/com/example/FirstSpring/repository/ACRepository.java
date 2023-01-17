package com.example.FirstSpring.repository;

import com.example.FirstSpring.models.AC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ACRepository extends JpaRepository<AC, Long> {
    Optional<AC> findById(Long ID);
}
