package com.example.FirstSpring.repository;

import com.example.FirstSpring.models.AC;
import com.example.FirstSpring.models.User;
import com.example.FirstSpring.models.UserAC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserACRepository extends JpaRepository<UserAC, Long> {
    List<UserAC> findByUser(User user);
    List<UserAC> findByAC(AC ac);

    Optional<UserAC> findById(Long id);
    UserAC findByUserAndAC(User user, AC ac);
}
