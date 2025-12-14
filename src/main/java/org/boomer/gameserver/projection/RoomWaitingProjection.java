package org.boomer.gameserver.projection;

import java.time.LocalDateTime;

public interface RoomWaitingProjection {
    Integer getId();
    String getRoomCode();
    Integer getOwnerId();
    String getUsername();
    String getStatus();
    Integer getMaxPlayers();
    LocalDateTime getCreatedAt();
    Integer getPlayerCount();
}
