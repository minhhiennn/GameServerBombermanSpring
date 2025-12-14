package org.boomer.gameserver.message;

import org.boomer.gameserver.controller.websocket.RoomEnum;

public class RoomCreateMessage extends RoomMessage {
    private RoomEnum type = RoomEnum.CREATE;

    public RoomCreateMessage(String roomCode, String userName) {
        super(roomCode, userName);
    }
    public RoomEnum getType() {
        return type;
    }
}
