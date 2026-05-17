package com.example.mediaservice.service;

import com.example.mediaservice.model.User;
import com.example.mediaservice.repository.AlbumRepository;
import com.example.mediaservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    public UserService(UserRepository userRepository, com.example.mediaservice.repository.AlbumRepository albumRepository) {
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
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



}