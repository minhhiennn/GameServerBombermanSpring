package org.boomer.gameserver.message;

import org.boomer.gameserver.controller.websocket.RoomEnum;

public class RoomJoinMessage extends RoomMessage {
    private RoomEnum type = RoomEnum.JOIN;

    public RoomJoinMessage(String roomCode, String userName) {
        super(roomCode, userName);
    }

    public RoomEnum getType() {
        return type;
    }
}
