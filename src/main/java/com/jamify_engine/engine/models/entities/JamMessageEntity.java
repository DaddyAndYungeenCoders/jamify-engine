package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jam_message")
public class JamMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jam_message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jam_id")
    private JamEntity jam;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity author;

    @Column(name = "jam_message_content")
    private String content;

    @Column(name = "jam_message_timestamp")
    private LocalDateTime timestamp;
}
