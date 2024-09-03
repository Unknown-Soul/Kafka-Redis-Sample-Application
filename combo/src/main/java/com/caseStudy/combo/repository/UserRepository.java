package com.caseStudy.combo.repository;

import com.caseStudy.combo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    // You can add custom query methods here if needed
}