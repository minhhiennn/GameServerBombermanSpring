package org.boomer.gameserver.services;

import org.boomer.gameserver.controller.websocket.RoomEnum;
import org.boomer.gameserver.entities.Room;
import org.boomer.gameserver.entities.RoomPlayer;
import org.boomer.gameserver.entities.User;
import org.boomer.gameserver.message.RoomCreateMessage;
import org.boomer.gameserver.message.RoomJoinMessage;
import org.boomer.gameserver.message.RoomLeaveMessage;
import org.boomer.gameserver.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {
    public WebSocketService() {
    }

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String username, WebSocketSession session) {
        sessions.put(username, session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }


    public void notifyRoomUpdate(RoomEnum updateType, Room room, User user) {
        System.out.println("Notifying room update: " + updateType + " for room " + room.getRoomCode() + " by user " + user.getUsername());
        switch (updateType) {
            case CREATE -> {
                String ownerName = user.getUsername();

                for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                    String key = entry.getKey();
                    WebSocketSession session = entry.getValue();

                    if (key.equals(ownerName)) {
                        continue;
                    }

                    if (session.isOpen()) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            RoomCreateMessage msg = new RoomCreateMessage(room.getRoomCode(), ownerName);
                            String json = mapper.writeValueAsString(msg);
                            session.sendMessage(new TextMessage(json));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // owner never join room, only players join room
            // so player join room always role is PLAYER
            case JOIN -> {
                String playerName = user.getUsername();

                for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                    String key = entry.getKey();
                    WebSocketSession session = entry.getValue();

                    if (key.equals(playerName)) {
                        continue;
                    }

                    if (session.isOpen()) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            RoomJoinMessage msg = new RoomJoinMessage(room.getRoomCode(), playerName);
                            String json = mapper.writeValueAsString(msg);
                            session.sendMessage(new TextMessage(json));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // leave can be by owner or player
            // if owner leave, all players will be notified owner leave
            case LEAVE -> {
                String username = user.getUsername();
                System.out.println("User leaving: " + username);

                boolean isOwnerLeaving = room.getOwnerId().equals(user.getId());
                System.out.println("Is owner leaving: " + isOwnerLeaving);

                for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                    String key = entry.getKey();
                    WebSocketSession session = entry.getValue();

                    if (key.equals(username)) {
                        continue;
                    }

                    if (session.isOpen()) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            RoomLeaveMessage msg = new RoomLeaveMessage(room.getRoomCode(), username, isOwnerLeaving);
                            String json = mapper.writeValueAsString(msg);
                            session.sendMessage(new TextMessage(json));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }
}