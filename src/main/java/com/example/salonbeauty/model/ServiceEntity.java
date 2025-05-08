// src/main/java/com/example/salonbeauty/model/ServiceEntity.java
package com.example.salonbeauty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    /** Цена */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    /** Длительность в минутах */
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /** Мастер, закреплённый за этой услугой */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "master_id", nullable = false)
    private User master;
}
