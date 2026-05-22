package com.example.mediaservice.dto;

public class SearchItemDTO {
    private Integer id;       // Уникальный ID сущности
    private String type;     // ТИП: "TRACK", "ALBUM", "ARTIST", "PLAYLIST"
    private String title;    // Название или Имя артиста
    private String subtitle; // Доп. инфо (имя исполнителя для трека, жанр/автор для альбома)
    private String imageUrl; // Ссылка на обложку / аватарку

    // 1. Пустой конструктор (обязателен для работы сериализаторов Jackson в Spring Boot)
    public SearchItemDTO() {
    }

    // 2. Полный конструктор для удобного создания объектов в репозиториях/сервисах
    public SearchItemDTO(Integer id, String type, String title, String subtitle, String imageUrl) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
    }

    // 3. Геттеры и Сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}