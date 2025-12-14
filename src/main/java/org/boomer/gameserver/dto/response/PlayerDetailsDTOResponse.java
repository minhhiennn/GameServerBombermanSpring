package org.boomer.gameserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.boomer.gameserver.entities.RoomPlayerRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDetailsDTOResponse {
    String username;
    RoomPlayerRole role;
}
