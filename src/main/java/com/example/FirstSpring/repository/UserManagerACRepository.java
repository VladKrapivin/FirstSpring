package com.example.FirstSpring.repository;

import com.example.FirstSpring.models.AC;
import com.example.FirstSpring.models.User;
import com.example.FirstSpring.models.UserManagerAC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserManagerACRepository extends JpaRepository<UserManagerAC, Long> {
    UserManagerAC findByUserAndAC(User user, AC ac);

    List<UserManagerAC> findByUser(User user);

    List<UserManagerAC> findByAC(AC ac);

    List<UserManagerAC> findByManager(User manager);
}
