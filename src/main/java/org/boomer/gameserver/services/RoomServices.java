package org.boomer.gameserver.services;

import jakarta.transaction.Transactional;
import org.boomer.gameserver.controller.websocket.RoomEnum;
import org.boomer.gameserver.dto.response.PlayerDetailsDTOResponse;
import org.boomer.gameserver.dto.response.RoomDetailsDTOResponse;
import org.boomer.gameserver.dto.response.RoomListDTOResponse;
import org.boomer.gameserver.entities.Room;
import org.boomer.gameserver.entities.RoomPlayer;
import org.boomer.gameserver.entities.RoomPlayerRole;
import org.boomer.gameserver.projection.RoomWaitingProjection;
import org.boomer.gameserver.repositories.RoomPlayerRepository;
import org.boomer.gameserver.repositories.RoomRepository;
import org.boomer.gameserver.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServices {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final WebSocketService webSocketService;
    public RoomServices(RoomRepository roomRepository, UserRepository userRepository, RoomPlayerRepository roomPlayerRepository, WebSocketService webSocketService) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomPlayerRepository = roomPlayerRepository;
        this.webSocketService = webSocketService;
    }

    public List<RoomListDTOResponse> getListRoom() {
        List<RoomWaitingProjection> rooms = roomRepository.getRoomList();
        List<RoomListDTOResponse> roomDTOs = new ArrayList<>();
        for (RoomWaitingProjection r : rooms) {
            roomDTOs.add(new RoomListDTOResponse(r.getRoomCode(), r.getUsername(), r.getPlayerCount(), r.getMaxPlayers()));
        }
        return roomDTOs;
    }

    public RoomDetailsDTOResponse getRoomDetails(String roomCode) {
        System.out.println("Fetching details for room code: " + roomCode);
        var roomDetails = new RoomDetailsDTOResponse();
        var room = roomRepository.findByRoomCode(roomCode);
        if (room.isPresent()) {
            roomDetails.setRoomCode(room.get().getRoomCode());
            List<RoomPlayer> roomPlayers = roomPlayerRepository.findByRoom_RoomCode(roomCode);
            roomPlayers.forEach(rp -> {
                System.out.println("Found player with userId: " + rp.getUserId() + " and role: " + rp.getRole());
                var player = new PlayerDetailsDTOResponse();
                player.setUsername(userRepository.findById(rp.getUserId()).get().getUsername());
                player.setRole(rp.getRole());
                roomDetails.getPlayers().add(player);
            });
        }
        System.out.println("Room details fetched: " + roomDetails);
        return roomDetails;
    }

    @Transactional
    public String createRoom(String username) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room newRoom = new Room();
        newRoom.setRoomCode(UUID.randomUUID().toString());
        newRoom.setOwnerId(user.getId());

        RoomPlayer newRoomPlayer = new RoomPlayer();
        newRoomPlayer.setRoom(newRoom);
        newRoomPlayer.setUserId(user.getId());
        newRoomPlayer.setRole(RoomPlayerRole.HOST);

        roomRepository.save(newRoom);
        roomPlayerRepository.save(newRoomPlayer);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        webSocketService.notifyRoomUpdate(
                                RoomEnum.CREATE,
                                newRoom,
                                user
                        );
                    }
                }
        );

        return newRoom.getRoomCode();
    }

    @Transactional
    public void joinRoom(String username, String roomCode) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        RoomPlayer newRoomPlayer = new RoomPlayer();
        newRoomPlayer.setRoom(room);
        newRoomPlayer.setUserId(user.getId());
        newRoomPlayer.setRole(RoomPlayerRole.PLAYER);

        roomPlayerRepository.save(newRoomPlayer);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        webSocketService.notifyRoomUpdate(
                                RoomEnum.JOIN,
                                room,
                                user
                        );
                    }
                }
        );
    }

    @Transactional
    public void leaveRoom(String username, String roomCode) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getOwnerId().equals(user.getId())) {
            roomPlayerRepository.deleteByRoom_Id(room.getId());
            roomRepository.delete(room);
        } else {
            roomPlayerRepository.deleteByRoom_IdAndUserId(
                    room.getId(),
                    user.getId()
            );
        }

        // ✅ thay Adapter bằng interface TransactionSynchronization
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        webSocketService.notifyRoomUpdate(
                                RoomEnum.LEAVE,
                                room,
                                user
                        );
                    }
                }
        );
    }
}
