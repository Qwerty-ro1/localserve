package com.localserve.localserve.repository;

import com.localserve.localserve.entity.Booking;
import com.localserve.localserve.entity.BookingStatus;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.entity.Provider;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByProvider(Provider provider);

    List<Booking> findByStatus(BookingStatus status);
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByProvider(Provider provider, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.serviceAddress = null WHERE b.serviceAddress.id = :addressId")
    void nullifyServiceAddress(@Param("addressId") Long addressId);

}