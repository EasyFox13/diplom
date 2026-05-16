package com.example.mediaservice.service;

import com.example.mediaservice.model.Track;
import com.example.mediaservice.model.TrackFeatures;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AudioAnalysisService {

    public TrackFeatures analyze(MultipartFile multipartFile, Track track) {
        TrackFeatures features = new TrackFeatures();
        features.setTrack(track);
        features.setTrackId(track.getTrackId());

        File tempFile = null;
        try {
            // Библиотека работает с файлами, поэтому создаем временный файл из потока
            tempFile = convertMultipartToFile(multipartFile);
            AudioFile f = AudioFileIO.read(tempFile);
            Tag tag = f.getTag();

            // 1. Извлекаем BPM (если есть в тегах)
            String bpmStr = tag.getFirst(FieldKey.BPM);
            features.setBpm(bpmStr.isEmpty() ? 0 : Integer.parseInt(bpmStr));

            // 2. Извлекаем Жанр
            features.setGenre(tag.getFirst(FieldKey.GENRE));

            // 3. Длительность в секундах
            int duration = f.getAudioHeader().getTrackLength();
            track.setDuration(duration); // Обновляем основную сущность трека

            // 4. Заглушки для продвинутых фич (энергичность и т.д.)
            // В реальном проекте здесь мог бы быть вызов Python-скрипта или библиотеки Librosa
            features.setEnergyLevel(0.7);
            features.setDanceability(0.5);
            features.setAcousticness(0.3);

        } catch (Exception e) {
            System.err.println("Ошибка анализа аудио: " + e.getMessage());
            // Устанавливаем дефолтные значения при ошибке
            features.setBpm(0);
            features.setGenre("Unknown");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // Удаляем временный файл
            }
        }

        return features;
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}