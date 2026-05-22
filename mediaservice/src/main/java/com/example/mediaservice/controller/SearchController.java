package com.example.mediaservice.controller;

import com.example.mediaservice.dto.SearchItemDTO;
import com.example.mediaservice.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository; // Добавили зависимость для поиска артистов
    private final ArtistRepository artistRepository;
    // Конструктор для внедрения всех репозиториев
    public SearchController(TrackRepository trackRepository,
                            AlbumRepository albumRepository,
                            PlaylistRepository playlistRepository,
                            UserRepository userRepository,
                            ArtistRepository artistRepository) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    @GetMapping
    public ResponseEntity<List<SearchItemDTO>> unifiedSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "ALL") String type) {

        List<SearchItemDTO> searchResults = new ArrayList<>();

        // 1. ПОИСК ПО ТРЕКАМ
        if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("TRACK")) {
            trackRepository.findByTitleContainingIgnoreCase(query).forEach(track -> {
                // Извлекаем имя артиста через цепочку Album -> Artist
                String artistName = (track.getAlbum() != null && track.getAlbum().getArtist() != null)
                        ? track.getAlbum().getArtist().getName()
                        : "Сингл";

                // ИСПРАВЛЕНО: Используем track.getTrackId() в соответствии с твоей моделью Track
                searchResults.add(new SearchItemDTO(
                        track.getTrackId() != null ? track.getTrackId() : null,
                        "TRACK",
                        track.getTitle(),
                        artistName,
                        null
                ));
            });
        }

        // 2. ПОИСК ПО АЛЬБОМАМ
        if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("ALBUM")) {
            albumRepository.findByTitleContainingIgnoreCase(query).forEach(album -> {
                searchResults.add(new SearchItemDTO(
                        album.getAlbumId() != null ? album.getAlbumId() : null,
                        "ALBUM",
                        album.getTitle(),
                        "Альбом",
                        album.getCoverUrl()
                ));
            });
        }

        // 3. ПОИСК ПО ПЛЕЙЛИСТАМ
        if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("PLAYLIST")) {
            playlistRepository.findByTitleContainingIgnoreCase(query).forEach(playlist -> {
                searchResults.add(new SearchItemDTO(
                        playlist.getPlaylistId() != null ? playlist.getPlaylistId() : null,
                        "PLAYLIST",
                        playlist.getTitle(),
                        "Авторский плейлист",
                        null
                ));
            });
        }

        // 4. ПОИСК ПО АРТИСТАМ
        if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("ARTIST")) {
            artistRepository.findByNameContainingIgnoreCase(query).forEach(artist -> {
                searchResults.add(new SearchItemDTO(
                        artist.getArtistId() != null ? artist.getArtistId() : null,
                        "ARTIST",
                        artist.getName(), // Берем красивый сценический псевдоним!
                        "Исполнитель",         // Субтитры для вывода на фронтенд
                        null                   // Сюда можно передать artist.getAvatarUrl() если есть в таблице
                ));
            });
        }

        return ResponseEntity.ok(searchResults);
    }
}