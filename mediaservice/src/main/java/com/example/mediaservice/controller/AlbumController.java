package com.example.mediaservice.controller;

import com.example.mediaservice.dto.AlbumFullResponseDTO;
import com.example.mediaservice.model.Album;
import com.example.mediaservice.model.Artist;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.ArtistRepository;
import com.example.mediaservice.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")
public class AlbumController {

    private final AlbumService albumService;
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

      @PostMapping
    public ResponseEntity<?> createAlbum(
            @RequestParam("artistId") Integer artistId, // Ловим ?artistId= из URL
            @RequestBody Album album // Ловим JSON альбома (title, coverUrl, releaseDate)
    ) {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            return ResponseEntity.badRequest().body("Ошибка: Артист с ID " + artistId + " не найден!");
        }
     album.setArtist(artist);

        Album savedAlbum = albumRepository.save(album);

        return ResponseEntity.ok(savedAlbum);
    }

     @GetMapping("/{id}")
    public ResponseEntity<AlbumFullResponseDTO> getAlbum(@PathVariable Integer id) {
        try {
            AlbumFullResponseDTO albumInfo = albumService.getAlbumFullInfo(id);
            return ResponseEntity.ok(albumInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        try {
            albumService.deleteAlbum(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        try {
            List<Album> albums = albumRepository.findAll();
            return ResponseEntity.ok(albums);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Album>> searchAlbums(@RequestParam("query") String query) {
        return ResponseEntity.ok(albumRepository.findByTitleContainingIgnoreCase(query));
    }
}