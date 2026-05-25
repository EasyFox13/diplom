package com.example.mediaservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_saved_albums", // Имя связующей таблицы в БД
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private List<Album> savedAlbums;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id") // или mappedBy, смотря где лежит внешний ключ
    @JsonManagedReference("user-artist")
    private Artist artist;

    @Column(name = "is_artist", nullable = false)
    private Boolean isArtist = false;
     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Playlist> playlists;
    public Boolean isArtist(){
        return isArtist;
    }

}