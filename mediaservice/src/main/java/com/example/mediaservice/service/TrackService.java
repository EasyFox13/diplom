package com.example.mediaservice.service;

import com.example.mediaservice.model.Album;
import com.example.mediaservice.model.Track;
import com.example.mediaservice.model.TrackFeatures;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final StorageService storageService;
    private final TrackRepository trackRepository;

    private final AudioAnalysisService audioAnalysisService;
    private final AlbumRepository albumRepository; // Добавляем

    @Autowired
    public TrackService(TrackRepository trackRepository,
                        StorageService storageService,
                        AudioAnalysisService audioAnalysisService,
                        AlbumRepository albumRepository) {
        this.trackRepository = trackRepository;
        this.storageService = storageService;
        this.audioAnalysisService = audioAnalysisService;
        this.albumRepository = albumRepository;
    }

//    public TrackService(StorageService storageService,
//                        TrackRepository trackRepository,
//                        AudioAnalysisService audioAnalysisService) {
//        this.storageService = storageService;
//        this.audioAnalysisService = audioAnalysisService;
//        this.trackRepository=trackRepository;
//    }


    @Transactional
    public Track createTrack(String title, Integer albumId, MultipartFile file) throws IOException {
        // 1. Ищем альбом в БД. Если ID передан, но альбома нет — кидаем ошибку.
        Album album = null;
        if (albumId != null) {
            album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Альбом с ID " + albumId + " не найден"));
        }

        // 2. Твоя текущая логика загрузки файла в MinIO
        String filePath = storageService.uploadFile(file);

        // 3. Создаем объект трека
        // ... (начало метода createTrack)

        Track track = new Track();
        track.setTitle(title);
        track.setFilePath(filePath);
        track.setCreatedAt(LocalDateTime.now());
        track.setAlbum(album); // Привязка альбома, которую мы добавили

        track.setBpm(0); // <-- ДОБАВЬ ЭТУ СТРОКУ, чтобы спасти Hibernate от null

// Поля для длительности (duration) тоже можно занулить по умолчанию, если они nullable = false
        track.setDuration(0);

// Теперь сохранение пройдет гладко!
        Track savedTrack = trackRepository.save(track);

// Тут сервис проанализирует файл и обновит 0 на реальный BPM
        audioAnalysisService.analyze(file,savedTrack);

        return savedTrack;
    }

    // Измени тип возвращаемого значения с InputStream на S3ObjectWrapper
    public StorageService.S3ObjectWrapper getTrackObject(Integer trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        return storageService.getFileObject(track.getFilePath());
    }
    public Page<Track> getAllTracks(Pageable pageable) {
        return trackRepository.findAll(pageable);
    }

    // 2. Получение одного трека по ID
    public Track getTrackById(Integer id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Трек с ID " + id + " не найден"));
    }


}