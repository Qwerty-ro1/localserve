package com.localserve.localserve.repository;

import com.localserve.localserve.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import com.localserve.localserve.entity.User;


import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUserId(Long userId);
    boolean existsByUser(User user);
    Optional<Provider> findByUser(User user);

    // Search providers by offering name
    List<Provider> findByOfferings_NameContainingIgnoreCase(String offeringName);
}