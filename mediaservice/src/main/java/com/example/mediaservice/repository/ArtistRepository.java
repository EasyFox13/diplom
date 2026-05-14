package com.example.mediaservice.repository;

import com.example.mediaservice.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    Long countByNameContainingIgnoreCase(String name);
}