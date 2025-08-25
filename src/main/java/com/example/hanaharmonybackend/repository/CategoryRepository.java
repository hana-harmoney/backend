package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
