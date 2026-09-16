package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "flag", schema = "ssv_db")
@Getter
@Setter
@NoArgsConstructor
public class FlagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flag_id_seq")
    @SequenceGenerator(name = "flag_id_seq", sequenceName = "flag_id_seq", schema = "ssv_db", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean defaultValue;

    @Column(nullable = false)
    private boolean withCustomization;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
