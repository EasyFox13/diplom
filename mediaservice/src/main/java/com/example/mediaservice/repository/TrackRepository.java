package com.example.mediaservice.repository;

import com.example.mediaservice.model.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    @EntityGraph(attributePaths = {"album"}) // Вытаскиваем трек сразу вместе с альбомом
    List<Track> findByTitleContainingIgnoreCase(String title);

    List<Track> findByBpmBetween(Integer minBpm, Integer maxBpm);
}