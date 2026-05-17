package com.example.mediaservice.dto;

import java.time.LocalDateTime;

public class TrackResponseDTO {
    private Integer trackId;
    private String title;
    private Integer duration;
    private String filePath;
    private Integer bpm;
    private LocalDateTime createdAt;

    // Конструктор для быстрой конвертации из сущности Track
    public TrackResponseDTO(Integer trackId, String title, Integer duration, String filePath, Integer bpm, LocalDateTime createdAt) {
        this.trackId = trackId;
        this.title = title;
        this.duration = duration;
        this.filePath = filePath;
        this.bpm = bpm;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры для всех полей
    public Integer getTrackId() { return trackId; }
    public void setTrackId(Integer trackId) { this.trackId = trackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Integer getBpm() { return bpm; }
    public void setBpm(Integer bpm) { this.bpm = bpm; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}