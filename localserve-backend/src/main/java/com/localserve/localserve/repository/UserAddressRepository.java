package com.localserve.localserve.repository;

import com.localserve.localserve.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserId(Long userId);

    Optional<UserAddress> findByUserIdAndDefaultAddressTrue(Long userId);

    long countByUserId(Long userId);
}