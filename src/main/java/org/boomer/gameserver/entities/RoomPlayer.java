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
@Table(name = "room_players")
@IdClass(RoomPlayerId.class)
public class RoomPlayer {

    @Id
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoomPlayerRole role = RoomPlayerRole.PLAYER;

    @Column(name = "joined_at", updatable = false, insertable = false)
    private LocalDateTime joinedAt;
}
