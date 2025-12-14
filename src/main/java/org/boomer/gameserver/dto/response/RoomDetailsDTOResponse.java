package org.boomer.gameserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailsDTOResponse {
    String roomCode;
    List<PlayerDetailsDTOResponse> players = new ArrayList<>();
}
