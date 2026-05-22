package com.example.mediaservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracks")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Integer trackId;

    @JsonBackReference("album-track")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    // --- ДОБАВЛЕНА СВЯЗЬ С АРТИСТОМ ---
    @JsonIgnoreProperties({"tracks", "albums", "hibernateLazyInitializer", "handler"}) // Игнорируем список треков внутри артиста, чтобы не было зацикливания
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false) // Имя колонки в БД (artist_id) из твоей структуры таблицы
    private Artist artist;
    // ----------------------------------

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath; // Это наш S3 Key или URI файла

    @Column(nullable = false)
    private Integer bpm;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TrackFeatures trackFeatures;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}