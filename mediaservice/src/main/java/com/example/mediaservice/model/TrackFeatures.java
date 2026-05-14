package com.example.mediaservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "track_features")
@Data
public class TrackFeatures {

    @Id
    @Column(name = "track_id")
    private Integer trackId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    private Track track;

    @Column(length = 50)
    private String genre;

    @Column(name = "bpm")
    private Integer bpm;

    @Column(name = "energy_level")
    private Double energyLevel;

    @Column(name = "danceability")
    private Double danceability;

    @Column(name = "acousticness")
    private Double acousticness;
}