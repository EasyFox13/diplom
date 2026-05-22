package com.example.mediaservice.dto;

import lombok.Data;

@Data
public class ArtistUpdateDTO {
    private String name;
    private String bio;
    private String imageUrl;
}