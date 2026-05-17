package com.example.mediaservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AlbumFullResponseDTO {
    // Геттеры и сеттеры
    private Integer albumId;
    private String title;
    private String coverUrl;
    private List<AlbumTrackInfoDTO> tracks;

    public AlbumFullResponseDTO(Integer albumId, String title, String coverUrl, List<AlbumTrackInfoDTO> tracks) {
        this.albumId = albumId;
        this.title = title;
        this.coverUrl = coverUrl;
        this.tracks = tracks;
    }

}