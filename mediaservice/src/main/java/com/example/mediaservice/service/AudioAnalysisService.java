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
            tempFile = convertMultipartToFile(multipartFile);
            AudioFile f = AudioFileIO.read(tempFile);
            Tag tag = f.getTag();
       String bpmStr = tag.getFirst(FieldKey.BPM);
            features.setBpm(bpmStr.isEmpty() ? 0 : Integer.parseInt(bpmStr));
      features.setGenre(tag.getFirst(FieldKey.GENRE));

             int duration = f.getAudioHeader().getTrackLength();
            track.setDuration(duration); // Обновляем основную сущность трека

              features.setEnergyLevel(0.7);
            features.setDanceability(0.5);
            features.setAcousticness(0.3);

        } catch (Exception e) {
            System.err.println("Ошибка анализа аудио: " + e.getMessage());
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