package com.example.mediaservice.service;

import com.example.mediaservice.dto.AlbumFullResponseDTO;
import com.example.mediaservice.dto.AlbumTrackInfoDTO;
import com.example.mediaservice.model.Album;
import com.example.mediaservice.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    // 1. Создание альбома
    @Transactional
    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    // 2. Удаление альбома
    @Transactional
    public void deleteAlbum(Integer id) {
        if (!albumRepository.existsById(id)) {
            throw new RuntimeException("Альбом с ID " + id + " не найден");
        }
        albumRepository.deleteById(id);
    }

    // 3. Получение карточки альбома со всеми его треками
    @Transactional(readOnly = true)
    public AlbumFullResponseDTO getAlbumFullInfo(Integer id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Альбом с ID " + id + " не найден"));

        // Маппим привязанные к альбому треки в легкие DTO
        List<AlbumTrackInfoDTO> trackDTOs = album.getTracks().stream()
                .map(track -> new AlbumTrackInfoDTO(
                        track.getTrackId(),
                        track.getTitle(),
                        track.getDuration(),
                        track.getBpm()
                ))
                .collect(Collectors.toList());

        // Возвращаем готовую карточку
        return new AlbumFullResponseDTO(album.getAlbumId(), album.getTitle(), album.getCoverUrl(), trackDTOs);
    }
}