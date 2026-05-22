package com.example.mediaservice.service;

import com.example.mediaservice.model.*;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.ArtistRepository;
import com.example.mediaservice.repository.TrackRepository;
import com.example.mediaservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final StorageService storageService;
    private final TrackRepository trackRepository;
    private final ArtistService artistService;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final AudioAnalysisService audioAnalysisService;
    private final AlbumRepository albumRepository; // Добавляем

    @Autowired
    public TrackService(TrackRepository trackRepository,
                        StorageService storageService, ArtistService artistService, ArtistRepository artistRepository, UserRepository userRepository,
                        AudioAnalysisService audioAnalysisService,
                        AlbumRepository albumRepository) {
        this.trackRepository = trackRepository;
        this.storageService = storageService;
        this.artistService = artistService;
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.audioAnalysisService = audioAnalysisService;
        this.albumRepository = albumRepository;
    }


    public List<Track> searchTracks(String query) {
        // Если в репозитории ещё нет такого метода, Spring Data JPA сгенерирует его автоматически по названию
        return trackRepository.findByTitleContainingIgnoreCase(query);
    }

    @Transactional
    public Track createTrack(String title, Integer bpm, Integer duration, Integer userId, Integer albumId, MultipartFile file) throws IOException {

        // 1. Ищем пользователя по его ID (который равен 1)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден"));

        // 2. Достаем связанного с ним артиста
        Artist artist = user.getArtist();
        if (artist == null) {
            throw new RuntimeException("Этот пользователь не является артистом!");
        }

        // 3. Ищем альбом
        Album album = null;
        if (albumId != null) {
            album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Альбом с ID " + albumId + " не найден"));
        }

        // ... дальше твоя неизменная логика сохранения трека
        String filePath = storageService.uploadFile(file);
        Track track = new Track();
        track.setTitle(title);
        track.setFilePath(filePath);
        track.setAlbum(album);
        track.setArtist(artist); // Передаем корректного артиста (у которого ID = 3)
        track.setBpm(bpm != null ? bpm : 0);
        track.setDuration(duration != null ? duration : 0);

        Track savedTrack = trackRepository.save(track);
        audioAnalysisService.analyze(file, savedTrack);

        return savedTrack;
    }


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