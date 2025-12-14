package org.boomer.gameserver.message;

public class RoomMessage {
    private String roomCode;
    private String userName;

    public RoomMessage(String roomCode, String userName) {
        this.roomCode = roomCode;
        this.userName = userName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getUserName() {
        return userName;
    }
}
