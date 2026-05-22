package com.example.mediaservice.service;

import com.example.mediaservice.model.Artist;
import com.example.mediaservice.model.User;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.ArtistRepository;
import com.example.mediaservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    public UserService(UserRepository userRepository, com.example.mediaservice.repository.AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }
    // Логика регистрации
    public User registerUser(User user) {
        // Проверяем, нет ли уже пользователя с таким email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован!");
        }

        // Пока сохраняем пароль напрямую в passwordHash (без шифрования для быстрого старта)
        user.setPasswordHash(user.getPasswordHash());

        return userRepository.save(user);
    }

    // Логика входа
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email не найден!"));

        // Проверяем совпадение паролей
        if (!user.getPasswordHash().equals(password)) {
            throw new RuntimeException("Неверный пароль!");
        }

        return user; // Возвращаем пользователя, если всё ок
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    public void saveAlbumToUser(Integer userId, Integer albumId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        com.example.mediaservice.model.Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Альбом не найден"));

        // Проверяем, не добавлен ли уже этот альбом, чтобы не дублировать
        if (!user.getSavedAlbums().contains(album)) {
            user.getSavedAlbums().add(album);
            userRepository.save(user); // Обновляем юзера в базе вместе со связью
        }
    }
    @Transactional
    public Artist makeUserAnArtist(Integer userId) {
        // 1. Ищем пользователя по id (используем имя поля userId из твоей модели)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден"));

        // Если пользователь уже является артистом, просто возвращаем его профиль
        if (user.getIsArtist()) {
            return artistRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Профиль артиста отсутствует при активном флаге is_artist"));
        }

        // 2. Выставляем флаг в true и сохраняем юзера
        user.setIsArtist(true);
        userRepository.save(user);

        // 3. Создаем сопутствующую запись в таблице artists
        Artist artist = new Artist();
        artist.setUser(user); // Привязываем OneToOne сущность User
        artist.setName(user.getUsername()); // По умолчанию псевдоним совпадает с юзернеймом
        artist.setBio("Биография исполнителя " + user.getUsername());
        artist.setImageUrl(null); // Изначально аватарки нет

        return artistRepository.save(artist);
    }


}