package com.example.mediaservice.controller;

import com.example.mediaservice.dto.AlbumFullResponseDTO;
import com.example.mediaservice.model.Album;
import com.example.mediaservice.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    // POST /api/albums - Создать новый альбом
    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestBody Album album) {
        Album created = albumService.createAlbum(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/albums/{id} - Получить карточку альбома со списком треков
    @GetMapping("/{id}")
    public ResponseEntity<AlbumFullResponseDTO> getAlbum(@PathVariable Integer id) {
        try {
            AlbumFullResponseDTO albumInfo = albumService.getAlbumFullInfo(id);
            return ResponseEntity.ok(albumInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE /api/albums/{id} - Удалить альбом
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        try {
            albumService.deleteAlbum(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}