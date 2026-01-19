package org.boomer.gameserver.message;

import org.boomer.gameserver.controller.websocket.RoomEnum;

public class RoomStartMessage {
    private RoomEnum type = RoomEnum.START;
    private String roomCode;
    private int matchId;

    private String gameServerHost;
    private int gameServerPort;

    public RoomStartMessage(String roomCode, int matchId, String gameServerHost, int gameServerPort) {
        this.roomCode = roomCode;
        this.matchId = matchId;
        this.gameServerHost = gameServerHost;
        this.gameServerPort = gameServerPort;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public RoomEnum getType() {
        return type;
    }

    public void setType(RoomEnum type) {
        this.type = type;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public String getGameServerHost() {
        return gameServerHost;
    }

    public void setGameServerHost(String gameServerHost) {
        this.gameServerHost = gameServerHost;
    }

    public int getGameServerPort() {
        return gameServerPort;
    }

    public void setGameServerPort(int gameServerPort) {
        this.gameServerPort = gameServerPort;
    }
}
