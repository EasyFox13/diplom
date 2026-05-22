package com.example.mediaservice.controller;

import com.example.mediaservice.model.Artist;
import com.example.mediaservice.model.User;
import com.example.mediaservice.repository.UserRepository;
import com.example.mediaservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.mediaservice.model.Album;
import com.example.mediaservice.repository.AlbumRepository; // если нужен для поиска альбома

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Позволяет фронтенду слать запросы без CORS-проблем
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // Эндпоинт для регистрации
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Эндпоинт для входа
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            User user = userService.loginUser(email, password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/saved-albums")
    public ResponseEntity<?> getSavedAlbums(@PathVariable Integer userId) {
        try {
            User user = userService.getUserById(userId); // Метод getUserById надо будет добавить в UserService
            return ResponseEntity.ok(user.getSavedAlbums());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/save-album/{albumId}")
    public ResponseEntity<?> saveAlbumToLibrary(@PathVariable Integer userId, @PathVariable Integer albumId) {
        try {
            userService.saveAlbumToUser(userId, albumId); // Этот метод мы сейчас напишем ниже
            return ResponseEntity.ok(Map.of("message", "Альбом успешно сохранен в медиатеку"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Если юзера нет в БД — вернет 404
    }
    // POST-запрос на адрес /api/users/{id}/become-artist
    @PostMapping("/{userId}/become-artist")
    public ResponseEntity<Artist> becomeArtist(@PathVariable Integer userId) {
        Artist artistProfile = userService.makeUserAnArtist(userId);
        return ResponseEntity.ok(artistProfile);
    }


}
