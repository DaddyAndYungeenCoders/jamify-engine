package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Event;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "email"})
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

    /**
     * @deprecated use user.jams with a filter on status running instead
     */
    @Deprecated(forRemoval = true)
    @Column(name = "user_has_jam_running")
    private boolean hasJamRunning;

    @OneToMany(mappedBy = "author")
    private Set<PlaylistEntity> playlists;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<EventEntity> events;

    @OneToMany(mappedBy = "user")
    private Set<JamParticipantEntity> jams;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventEntity> hostedEvents;

}

// TODO: Un user ici avec les infos de l'app (jam, event etc...) et un user_uaa avec les infos de l'uaa (email, role etc...) ?
