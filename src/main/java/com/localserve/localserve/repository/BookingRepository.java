package com.localserve.localserve.repository;

import com.localserve.localserve.entity.Booking;
import com.localserve.localserve.entity.BookingStatus;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByProvider(Provider provider);

    List<Booking> findByStatus(BookingStatus status);
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByProvider(Provider provider, Pageable pageable);

}