package com.example.mediaservice.service;

import com.example.mediaservice.model.Track;
import com.example.mediaservice.model.TrackFeatures;
import com.example.mediaservice.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final StorageService storageService;
    private final TrackRepository trackRepository;

    private final AudioAnalysisService audioAnalysisService;

//    public TrackService(StorageService storageService,
//                        TrackRepository trackRepository,
//                        AudioAnalysisService audioAnalysisService) {
//        this.storageService = storageService;
//        this.audioAnalysisService = audioAnalysisService;
//        this.trackRepository=trackRepository;
//    }


    @Transactional
    public Track createTrack(String title, Integer duration, MultipartFile file) throws IOException {
        // 1. Загружаем в S3
        String s3Key = storageService.uploadFile(file);

        // 2. Создаем сущность трека
        Track track = new Track();
        track.setTitle(title);
        track.setDuration(duration);
        track.setFilePath(s3Key);
        track.setBpm(0); // Заглушка, пока не вызвали парсер

        // 3. Сохраняем (JPA автоматически создаст ID)
        track = trackRepository.save(track);

        // 4. Анализируем аудио и создаем TrackFeatures
        // Передаем файл или поток в сервис анализа
        TrackFeatures features = audioAnalysisService.analyze(file, track);
        track.setTrackFeatures(features);
        track.setBpm(features.getBpm());

        return trackRepository.save(track);
    }
}