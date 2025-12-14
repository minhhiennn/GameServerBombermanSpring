package org.boomer.gameserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomListDTOResponse {
    private String roomCode;
    private String ownerUsername;
    private int playerCount;
    private int maxPlayers;
}
