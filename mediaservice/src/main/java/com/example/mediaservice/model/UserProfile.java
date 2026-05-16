package com.example.mediaservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    // На схеме указан тип character varying[](100) - это массив строк в Postgres
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "top_genres", columnDefinition = "text[]")
    private String[] topGenres;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}