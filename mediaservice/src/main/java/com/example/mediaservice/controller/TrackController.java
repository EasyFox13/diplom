package com.example.mediaservice.controller;

import com.example.mediaservice.model.Track;
import com.example.mediaservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping("/upload")
    public ResponseEntity<Track> uploadTrack(
            @RequestParam("title") String title,
            @RequestParam("albumId") Integer albumId, // ДОБАВЬ ЭТО
            @RequestParam("file") MultipartFile file) {
        try {
            // Передаем albumId в сервис
            Track track = trackService.createTrack(title, albumId, file);
            return ResponseEntity.ok(track);
        } catch (Exception e) {
            e.printStackTrace(); // Чтобы видеть ошибку в консоли, если упадет снова
            return ResponseEntity.internalServerError().build();
        }
    }
}