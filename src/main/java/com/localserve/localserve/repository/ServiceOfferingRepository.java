package com.localserve.localserve.repository;

import com.localserve.localserve.entity.ServiceOffering;
import com.localserve.localserve.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    // Get all services offered by a provider
    List<ServiceOffering> findByProvider(Provider provider);
    List<ServiceOffering> findByName(String name);
}