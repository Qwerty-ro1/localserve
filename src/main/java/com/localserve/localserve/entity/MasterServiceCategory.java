package com.localserve.localserve.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "master_service_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}