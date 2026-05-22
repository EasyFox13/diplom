package com.example.mediaservice.controller;

import com.example.mediaservice.model.Playlist;
import com.example.mediaservice.repository.PlaylistRepository;
import com.example.mediaservice.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Чтобы не было проблем с CORS политикой браузера
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;

    // 1. Обработка GET запроса: /api/users/{userId}/playlists
    @GetMapping("/users/{userId}/playlists")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Integer userId) {
        List<Playlist> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }
    @PostMapping("/playlists/{playlistId}/add-track/{trackId}")
    public ResponseEntity<?> addTrackToPlaylist(
            @PathVariable("playlistId") Integer playlistId,
            @PathVariable("trackId") Integer trackId) {


        playlistService.addTrackToPlaylist(playlistId, trackId);

        return ResponseEntity.ok().body("Трек успешно добавлен в плейлист");
    }
    // 2. Обработка POST запроса: /api/playlists?userId=3&title=xxx
    @PostMapping("/playlists")
    public ResponseEntity<Playlist> createPlaylist(
            @RequestParam Integer userId,
            @RequestParam String title) {
        Playlist created = playlistService.createPlaylist(userId, title);
        return ResponseEntity.ok(created);
    }
    @GetMapping("/playlists/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Integer id) {
        return playlistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // 1. Обновление данных плейлиста (PUT /api/playlists/{id})
    @PutMapping("/playlists/{id}")
    public ResponseEntity<Playlist> updatePlaylist(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String title = body.get("title");
        String coverUrl = body.get("coverUrl");
        Playlist updated = playlistService.updatePlaylist(id, title, coverUrl);
        return ResponseEntity.ok(updated);
    }

    // 2. Удаление плейлиста (DELETE /api/playlists/{id})
    @DeleteMapping("/playlists/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Integer id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    // 3. Удаление трека из плейлиста (DELETE /api/playlists/{playlistId}/remove-track/{trackId})
    @DeleteMapping("/playlists/{playlistId}/remove-track/{trackId}")
    public ResponseEntity<?> removeTrackFromPlaylist(
            @PathVariable Integer playlistId,
            @PathVariable Integer trackId) {
        playlistService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.ok().body(Map.of("message", "Трек успешно удален из плейлиста"));
    }
    
}