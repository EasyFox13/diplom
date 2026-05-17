package com.example.mediaservice.repository;

import com.example.mediaservice.model.Album; // Убедись, что путь к твоей сущности Album верный
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    // Базовые методы типа findById, save, delete уже встроены благодаря JpaRepository
}