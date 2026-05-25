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
    @Transactional(readOnly = true)
    public List<Playlist> getPlaylistsByUserId(Integer userId) {
        return playlistRepository.findByUserUserId(userId);
    }

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

    @Transactional
    public void addTrackToPlaylist(Integer playlistId, Integer trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

       playlist.getTracks().add(track);

        playlistRepository.save(playlist);
    }
    @Transactional
    public Playlist updatePlaylist(Integer playlistId, String title, String coverUrl) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));
        playlist.setTitle(title);
        playlist.setCoverUrl(coverUrl);
        return playlistRepository.save(playlist);
    }

     @Transactional
    public void deletePlaylist(Integer playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new RuntimeException("Плейлист не найден");
        }
        playlistRepository.deleteById(playlistId);
    }

     @Transactional
    public void removeTrackFromPlaylist(Integer playlistId, Integer trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));

         playlist.getTracks().removeIf(track -> track.getTrackId().equals(trackId));

        playlistRepository.save(playlist);
    }
}