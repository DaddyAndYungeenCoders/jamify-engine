package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

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

    @Column(name = "user_email")
    private @NotNull String email;

    @Column(name = "user_name")
    private @NotNull String name;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "user_role")
    private Set<String> roles;

    @Column(name = "user_country")
    private String country;

    @Column(name = "user_provider")
    private String provider;

    @Column(name = "user_provider_id")
    private String userProviderId;

    @Column(name = "user_img_url", length = 1024) // for long imgurl (eg. spotify)
    private String imgUrl;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAccessTokenEntity accessToken;

    @Column(name = "user_has_jam_running")
    private boolean hasJamRunning;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JamEntity> hostedJams;

    @OneToMany(mappedBy = "author")
    private Set<PlaylistEntity> playlists;

    @OneToOne(fetch = FetchType.EAGER)
    private JamEntity currentJam;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<EventEntity> events;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventEntity> hostedEvents;


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
