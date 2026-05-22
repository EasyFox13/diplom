package com.example.mediaservice.controller; // Укажи свой пакет

import com.example.mediaservice.dto.AlbumCreateDTO;
import com.example.mediaservice.dto.ArtistUpdateDTO;
import com.example.mediaservice.model.Album;
import com.example.mediaservice.model.Artist; // Укажи свои модели
import com.example.mediaservice.repository.ArtistRepository;
import com.example.mediaservice.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*") // Чтобы фронтенд не ругался на CORS
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;
    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // GET /api/artists — получить список всех для выпадающего списка
    @GetMapping
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    // POST /api/artists — создать нового артиста (то, что выдало 404)
    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestBody Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        return ResponseEntity.ok(savedArtist);
    }
    @GetMapping("/{artistId}/albums")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable Integer artistId) {
        // Ищешь артиста в БД, берешь его список альбомов и возвращаешь
        return artistRepository.findById(artistId)
                .map(artist -> ResponseEntity.ok(artist.getAlbums()))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Artist> getProfile(@PathVariable Integer userId) {
        return ResponseEntity.ok(artistService.getProfileByUserId(userId));
    }

    // Изменить био / псевдоним / аватарку артиста
    @PutMapping("/profile/{userId}")
    public ResponseEntity<Artist> updateProfile(
            @PathVariable Integer userId,
            @RequestBody ArtistUpdateDTO dto) {

        Artist updatedArtist = artistService.updateProfile(
                userId,
                dto.getName(),
                dto.getBio(),
                dto.getImageUrl()
        );
        return ResponseEntity.ok(updatedArtist);
    }

    // Создать новый альбом
    @PostMapping("/albums/{userId}")
    public ResponseEntity<Album> createAlbum(
            @PathVariable Integer userId,
            @RequestBody AlbumCreateDTO dto) {

        Album newAlbum = artistService.createAlbum(userId, dto.getTitle(), dto.getCoverUrl());
        return ResponseEntity.ok(newAlbum);
    }

}