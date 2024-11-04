package com.accepted.givutake.global.repository;

import com.accepted.givutake.global.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories,Integer> {
}
