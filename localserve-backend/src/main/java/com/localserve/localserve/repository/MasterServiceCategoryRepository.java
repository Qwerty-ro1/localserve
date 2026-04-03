package com.localserve.localserve.repository;

import com.localserve.localserve.entity.MasterServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterServiceCategoryRepository extends JpaRepository<MasterServiceCategory, Long> {

    Optional<MasterServiceCategory> findByName(String name);
}