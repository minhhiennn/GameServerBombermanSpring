package org.boomer.gameserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @PrePersist
    public void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}