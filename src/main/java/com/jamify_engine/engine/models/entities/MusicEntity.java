package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "music")
public class MusicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    @Column(name = "music_src")
    private String src;
    @Column(name = "music_author")
    private String author;
    @Column(name = "music_title")
    private String title;
    @Column(name = "music_image_src")
    private String imgUrl;
    @Column(name = "music_tempo")
    private String tempo;
    @Column(name = "music_energy")
    private String energy;

    @ManyToMany(mappedBy = "musics")
    private Set<PlaylistEntity> playlists;

    @ManyToMany
    @JoinTable(
            name = "music_tag",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags;
}
