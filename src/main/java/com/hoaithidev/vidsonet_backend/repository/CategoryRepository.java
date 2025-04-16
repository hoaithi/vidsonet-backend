package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
