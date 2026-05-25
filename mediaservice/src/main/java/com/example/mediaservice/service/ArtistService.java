package com.example.mediaservice.service;

import com.example.mediaservice.model.Album;
import com.example.mediaservice.model.Artist;
import com.example.mediaservice.model.User;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.ArtistRepository;
import com.example.mediaservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public ArtistService(ArtistRepository artistRepository,
                         AlbumRepository albumRepository,
                         UserRepository userRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Artist updateProfile(Integer userId, String name, String bio, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("У вас нет профиля артиста"));

        if (name != null && !name.trim().isEmpty()) {
            artist.setName(name);
        }
        artist.setBio(bio);
        if (imageUrl != null) {
            artist.setImageUrl(imageUrl);
        }

        return artistRepository.save(artist);
    }

    @Transactional
    public Album createAlbum(Integer userId, String title, String coverUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Только пользователи со статусом артиста могут создавать альбомы"));

        Album album = new Album();
        album.setTitle(title);
        album.setCoverUrl(coverUrl);
        album.setArtist(artist); // Привязываем альбом строго к сущности Artist

        return albumRepository.save(album);
    }

     public Artist getProfileByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Профиль артиста не найден"));
    }
}