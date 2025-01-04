package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JamEntity> hostedJams;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_has_jam_running")
    private boolean hasJamRunning;

    @OneToMany(mappedBy = "author")
    private Set<PlaylistEntity> playlists;

/* FIXME remove comments and create badge entity (com.jamify_engine.engine.models.entities.BadgeEntity)
    @ManyToMany
    @JoinTable(
            name = "badge_detention",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private Set<BadgeEntity> badges;
 */
}

// TODO: Un user ici avec les infos de l'app (jam, event etc...) et un user_uaa avec les infos de l'uaa (email, role etc...) ?
