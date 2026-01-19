package org.boomer.gameserver.services;

import org.boomer.gameserver.controller.websocket.RoomEnum;
import org.boomer.gameserver.entities.*;
import org.boomer.gameserver.message.RoomCreateMessage;
import org.boomer.gameserver.message.RoomJoinMessage;
import org.boomer.gameserver.message.RoomLeaveMessage;
import org.boomer.gameserver.message.RoomStartMessage;
import org.boomer.gameserver.repositories.RoomPlayerRepository;
import org.boomer.gameserver.repositories.RoomRepository;
import org.boomer.gameserver.repositories.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class WebSocketService {

    // username -> session
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // session -> username (NEW)
    private final Map<WebSocketSession, String> sessionToUser = new ConcurrentHashMap<>();

    // heartbeat
    private final Map<WebSocketSession, Long> lastPingMap = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 15_000; // 15s

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomPlayerRepository roomPlayerRepository;

    public WebSocketService(
            RoomRepository roomRepository,
            UserRepository userRepository,
            RoomPlayerRepository roomPlayerRepository
    ) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomPlayerRepository = roomPlayerRepository;
    }

    /* =========================
       Heartbeat
     ========================= */

    @Scheduled(fixedRate = 5000)
    public void checkHeartbeat() {
        System.out.println("Checking heartbeat for " + lastPingMap.size() + " sessions");
        long now = System.currentTimeMillis();
        List<WebSocketSession> timeoutSessions = new ArrayList<>();

        for (var entry : lastPingMap.entrySet()) {
            if (now - entry.getValue() > TIMEOUT) {
                timeoutSessions.add(entry.getKey());
            }
        }

        for (WebSocketSession session : timeoutSessions) {
            handleTimeout(session);
        }
    }

    private void handleTimeout(WebSocketSession session) {
        String username = sessionToUser.get(session);

        try {
            System.out.println("Heartbeat timeout -> closing session: " + username);
            session.close(CloseStatus.SESSION_NOT_RELIABLE);
        } catch (Exception ignored) {
        }

        if (username != null) {
            handleUserDisconnect(username);
        }

        removeSession(session);
    }

    public void onPing(WebSocketSession session) {
        lastPingMap.put(session, System.currentTimeMillis());
    }

    /* =========================
       Session management
     ========================= */

    public void addSession(String username, WebSocketSession session) {
        sessions.put(username, session);
        sessionToUser.put(session, username);
        lastPingMap.put(session, System.currentTimeMillis());
    }

    public void removeSession(WebSocketSession session) {
        String username = sessionToUser.remove(session);
        if (username != null) {
            sessions.remove(username);
        }
        lastPingMap.remove(session);
    }

    /* =========================
       Disconnect logic (GIỮ LOGIC CŨ)
     ========================= */

    private void handleUserDisconnect(String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        var roomPlayerOpt = roomPlayerRepository.findByUserId(user.getId());

        if (roomPlayerOpt.isEmpty()) return;

        RoomPlayer roomPlayer = roomPlayerOpt.get();
        RoomPlayerRole role = roomPlayer.getRole();
        Room room = roomPlayer.getRoom();

        if (role == RoomPlayerRole.HOST) {
            roomRepository.delete(room);
            System.out.println("Room " + room.getRoomCode() + " deleted due to host disconnect");
            notifyRoomUpdate(RoomEnum.LEAVE, room, user);
        } else {
            roomPlayerRepository.deleteByRoom_IdAndUserId(room.getId(), user.getId());
            System.out.println("User " + username + " removed from room " + room.getRoomCode());
            notifyRoomUpdate(RoomEnum.LEAVE, room, user);
        }
    }

    /* =========================
       Notify
     ========================= */

    public void notifyRoomUpdate(RoomEnum updateType, Room room, User user) {
        System.out.println(
                "Notifying room update: " + updateType +
                        " for room " + room.getRoomCode() +
                        " by user " + user.getUsername()
        );

        ObjectMapper mapper = new ObjectMapper();

        for (var entry : sessions.entrySet()) {
            String targetUsername = entry.getKey();
            WebSocketSession session = entry.getValue();

            if (targetUsername.equals(user.getUsername())) continue;
            if (!session.isOpen()) continue;

            try {
                String json = switch (updateType) {
                    case CREATE -> mapper.writeValueAsString(
                            new RoomCreateMessage(room.getRoomCode(), user.getUsername())
                    );
                    case JOIN -> mapper.writeValueAsString(
                            new RoomJoinMessage(room.getRoomCode(), user.getUsername())
                    );
                    case LEAVE -> mapper.writeValueAsString(
                            new RoomLeaveMessage(
                                    room.getRoomCode(),
                                    user.getUsername(),
                                    room.getOwnerId().equals(user.getId())
                            )
                    );
                    default -> throw new IllegalStateException("Unexpected value: " + updateType);
                };

                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyGameStart(Room room, Match match) {
        try {
            var players = roomPlayerRepository.findAllByRoom_Id(room.getId());
            ObjectMapper mapper = new ObjectMapper();

            System.out.println(players.size() + " players to notify for game start in room " + room.getRoomCode());

            for (var player : players) {
                String targetUsername = userRepository.findById(player.getUserId())
                        .map(User::getUsername)
                        .orElse(null);
                System.out.println("Notifying game start to: " + targetUsername);
                if (targetUsername == null) continue;

                WebSocketSession session = sessions.get(targetUsername);
                System.out.println(session);
                if (session == null || !session.isOpen()) continue;

                System.out.println("Session is open for user: " + targetUsername);

                try {
                    String json = mapper.writeValueAsString(
                            new RoomStartMessage(room.getRoomCode(), match.getId(), "localhost", 5056)
                    );

                    System.out.println("here is the json: " + json);

                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Error notifyGameStart: " + e.getMessage());
        }
    }
}
