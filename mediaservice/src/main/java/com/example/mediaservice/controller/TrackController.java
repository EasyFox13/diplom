package com.example.mediaservice.controller;

import com.example.mediaservice.model.Track;
import com.example.mediaservice.service.StorageService;
import com.example.mediaservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.mediaservice.dto.TrackResponseDTO;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam("query") String query) {
        return ResponseEntity.ok(trackService.searchTracks(query));
    }
    // ИСПРАВЛЕНО: Убрали "/upload", теперь эндпоинт слушает ровно: POST /api/tracks
//    @PostMapping
//    public ResponseEntity<Track> uploadTrack(
//            @RequestParam("title") String title,
//            @RequestParam("albumId") Integer albumId, // Изменили Integer на Long для надежности (согласно бд)
//            @RequestParam("file") MultipartFile file) {
//        try {
//            // Передаем albumId в сервис
//            // Если твой trackService принимает Integer, то либо приведи его к Long, либо оставь здесь Integer
//            Track track = trackService.createTrack(title, bpm, duration, artistId, albumId, file);
//            return ResponseEntity.ok(track);
//        } catch (Exception e) {
//            e.printStackTrace(); // Чтобы видеть ошибку в консоли, если упадет внутри сервиса
//            return ResponseEntity.internalServerError().build();
//        }
//    }
    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamingResponseBody> streamTrack(
            @PathVariable Integer id,
            @RequestHeader HttpHeaders headers) {
        try {
            StorageService.S3ObjectWrapper s3Object = trackService.getTrackObject(id);
            InputStream inputStream = s3Object.getInputStream();
            long fileLength = s3Object.getContentLength();
            List<HttpRange> ranges = headers.getRange();

            if (ranges.isEmpty()) {
                StreamingResponseBody responseBody = outputStream -> {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    try (inputStream) {
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                };

                return ResponseEntity.status(HttpStatus.OK)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(responseBody);
            }
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(fileLength);
            long end = range.getRangeEnd(fileLength);
            long rangeLength = end - start + 1;
            long skipped = inputStream.skip(start);
            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[4096];
                long bytesToRead = rangeLength;
                try (inputStream) {
                    while (bytesToRead > 0) {
                        int maxRead = (int) Math.min(buffer.length, bytesToRead);
                        int bytesRead = inputStream.read(buffer, 0, maxRead);
                        if (bytesRead == -1) break;
                        outputStream.write(buffer, 0, bytesRead);
                        bytesToRead -= bytesRead;
                    }
                }
            };
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) // 206
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength))
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadTrack(
            @RequestParam("title") String title,
            @RequestParam(value = "bpm", required = false) Integer bpm,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam("artistId") Integer artistId,
            @RequestParam(value = "albumId", required = false) Integer albumId,
            @RequestParam("file") MultipartFile file) {

        try {
           if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл не выбран или пуст");
            }

           trackService.createTrack(title, bpm, duration, artistId, albumId, file);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке трека на сервер: " + e.getMessage());
        }
    }


// ... (твой существующий код)

    // 1. Эндпоинт для получения списка треков (с пагинацией и сортировкой по дате добавления)
    @GetMapping
    public ResponseEntity<Page<TrackResponseDTO>> getAllTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        // Получаем тяжелые треки из сервиса
        Page<Track> tracksPage = trackService.getAllTracks(pageable);

        // Маппим (конвертируем) их в легкие DTO
        Page<TrackResponseDTO> dtoPage = tracksPage.map(track -> new TrackResponseDTO(
                track.getTrackId(),
                track.getTitle(),
                track.getDuration(),
                track.getFilePath(),
                track.getBpm(),
                track.getCreatedAt()
        ));

        return ResponseEntity.ok(dtoPage);
    }

    // 2. Получение деталей одного трека БЕЗ фич
    @GetMapping("/{id}")
    public ResponseEntity<TrackResponseDTO> getTrackById(@PathVariable Integer id) {
        try {
            Track track = trackService.getTrackById(id);

            TrackResponseDTO dto = new TrackResponseDTO(
                    track.getTrackId(),
                    track.getTitle(),
                    track.getDuration(),
                    track.getFilePath(),
                    track.getBpm(),
                    track.getCreatedAt()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
}