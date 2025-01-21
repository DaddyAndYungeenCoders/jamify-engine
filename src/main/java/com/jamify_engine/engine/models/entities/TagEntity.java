package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity(name = "tags")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_label", unique = true)
    private String label;

    @ManyToMany(mappedBy = "tags")
    private Set<MusicEntity> musics;
}