package com.example.mediaservice.service;

import com.example.mediaservice.model.Playlist;
import com.example.mediaservice.model.Track;
import com.example.mediaservice.model.User;
import com.example.mediaservice.repository.PlaylistRepository;
import com.example.mediaservice.repository.TrackRepository;
import com.example.mediaservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository; // Нужен для привязки плейлиста к юзеру
    private final TrackRepository trackRepository;
    // Получить все плейлисты конкретного пользователя
    @Transactional(readOnly = true)
    public List<Playlist> getPlaylistsByUserId(Integer userId) {
        return playlistRepository.findByUserUserId(userId);
    }

    // Создать новый плейлист
    @Transactional
    public Playlist createPlaylist(Integer userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setTitle(title);
        playlist.setIsPublic(true);

        return playlistRepository.save(playlist);
    }

    @Transactional // <-- КРИТИЧЕСКИ ВАЖНО! Без этого Hibernate не сделает коммит изменений
    public void addTrackToPlaylist(Integer playlistId, Integer trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        // Добавляем трек в коллекцию плейлиста
        playlist.getTracks().add(track);

        // На всякий случай сохраняем принудительно
        playlistRepository.save(playlist);
    }
    // 1. Обновление названия и обложки
    @Transactional
    public Playlist updatePlaylist(Integer playlistId, String title, String coverUrl) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));
        playlist.setTitle(title);
        playlist.setCoverUrl(coverUrl);
        return playlistRepository.save(playlist);
    }

    // 2. Полное удаление плейлиста
    @Transactional
    public void deletePlaylist(Integer playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new RuntimeException("Плейлист не найден");
        }
        playlistRepository.deleteById(playlistId);
    }

    // 3. Удаление трека из плейлиста (разрыв связи в таблице playlist_tracks)
    @Transactional
    public void removeTrackFromPlaylist(Integer playlistId, Integer trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));

        // Удаляем трек из коллекции плейлиста по его ID
        playlist.getTracks().removeIf(track -> track.getTrackId().equals(trackId));

        playlistRepository.save(playlist);
    }
}