package com.example.mediaservice.dto;

public class AlbumTrackInfoDTO {
    private Integer trackId;
    private String title;
    private Integer duration;
    private Integer bpm;

    public AlbumTrackInfoDTO(Integer trackId, String title, Integer duration, Integer bpm) {
        this.trackId = trackId;
        this.title = title;
        this.duration = duration;
        this.bpm = bpm;
    }

    // Геттеры и сеттеры
    public Integer getTrackId() { return trackId; }
    public void setTrackId(Integer trackId) { this.trackId = trackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getBpm() { return bpm; }
    public void setBpm(Integer bpm) { this.bpm = bpm; }
}