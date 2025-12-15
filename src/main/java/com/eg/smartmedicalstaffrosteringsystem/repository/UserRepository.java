package com.eg.smartmedicalstaffrosteringsystem.repository;


import com.eg.smartmedicalstaffrosteringsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNationalId(String nationalId);
    boolean existsByNationalId(String nationalId);
}
