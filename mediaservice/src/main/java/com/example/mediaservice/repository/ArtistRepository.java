package com.example.mediaservice.repository;

import com.example.mediaservice.model.Artist;
import com.example.mediaservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    List<Artist> findByNameContainingIgnoreCase(String name);

    // Добавь этот метод для связи OneToOne:
    Optional<Artist> findByUser(User user);
}