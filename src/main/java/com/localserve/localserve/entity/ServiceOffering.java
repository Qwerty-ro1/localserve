package com.localserve.localserve.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // e.g., "Electrician", "Plumber"

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}