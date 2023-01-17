package com.example.FirstSpring.repository;

import com.example.FirstSpring.models.AC;
import com.example.FirstSpring.models.TemporaryUserManager;
import com.example.FirstSpring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemporaryUserManagerRepository extends JpaRepository<TemporaryUserManager, Long> {

    TemporaryUserManager findByUserAndAC(User user, AC ac);

    List<TemporaryUserManager> findByUser(User user);

    List<TemporaryUserManager> findByAC(AC ac);
}
