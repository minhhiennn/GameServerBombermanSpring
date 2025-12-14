package org.boomer.gameserver.controller.restapi;

import org.boomer.gameserver.dto.request.UserDTORequest;
import org.boomer.gameserver.services.RoomServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final RoomServices roomServices;
    public RoomController(RoomServices roomServices) {
        this.roomServices = roomServices;
    }
    @GetMapping("/list")
    public ResponseEntity<?> getRoomsList() {
        try {
            var rooms = roomServices.getListRoom();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Failed to retrieve room list");
        }
    }
    @GetMapping("/roomDetails/{roomCode}")
    public ResponseEntity<?> getRoomDetails(@PathVariable String roomCode) {
        try {
            var roomDetails = roomServices.getRoomDetails(roomCode);
            System.out.println(roomDetails);
            return ResponseEntity.ok(roomDetails);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Failed to retrieve room details");
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String username) {
        try {
            String roomCode = roomServices.createRoom(username);
            return ResponseEntity.ok(roomCode);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Failed to create room");
        }
    }
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestHeader("Authorization") String username, @RequestBody String roomCode) {
        try {
            roomServices.joinRoom(username, roomCode.replaceAll("\"", ""));
            return ResponseEntity.ok("Join Room API is working");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Failed to join room");
        }
    }
    @PostMapping("/leave")
    public ResponseEntity<?> leaveRoom(@RequestHeader("Authorization") String username, @RequestBody String roomCode) {
        try {
            roomServices.leaveRoom(username, roomCode.replaceAll("\"", ""));
            return ResponseEntity.ok("Leave Room API is working");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Failed to leave room");
        }
    }
}
