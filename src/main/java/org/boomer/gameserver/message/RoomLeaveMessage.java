package org.boomer.gameserver.message;

import org.boomer.gameserver.controller.websocket.RoomEnum;

public class RoomLeaveMessage extends RoomMessage {
    private RoomEnum type = RoomEnum.LEAVE;

    private boolean isOwnerLeaving;

    public RoomLeaveMessage(String roomCode, String userName, boolean isOwnerLeaving) {
        super(roomCode, userName);
        this.isOwnerLeaving = isOwnerLeaving;
    }

    public RoomEnum getType() {
        return type;
    }

    public boolean isOwnerLeaving() {
        return isOwnerLeaving;
    }
}
