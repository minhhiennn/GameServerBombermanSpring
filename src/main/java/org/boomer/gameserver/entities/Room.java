package org.boomer.gameserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_code", length = 36, nullable = false, unique = true)
    private String roomCode;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoomStatus status = RoomStatus.WAITING;

    @Column(name = "max_players")
    private Integer maxPlayers = 4;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // Quan hệ 1-N với room_players
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomPlayer> roomPlayers;
}
