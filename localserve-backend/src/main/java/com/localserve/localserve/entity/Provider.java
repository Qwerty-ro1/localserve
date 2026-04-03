package com.localserve.localserve.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOffering> offerings;

    @OneToMany(mappedBy = "provider")
    @JsonIgnore
    private List<Booking> bookings;

    private String businessName;

    private String description;

    private int experienceYears;

    private double serviceRadius;

    private double rating;

    // location details
    private String location;
    private double latitude;
    private double longitude;


}