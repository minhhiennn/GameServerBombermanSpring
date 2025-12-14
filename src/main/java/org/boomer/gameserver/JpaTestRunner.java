package org.boomer.gameserver;

import jakarta.annotation.PostConstruct;
import org.boomer.gameserver.projection.RoomWaitingProjection;
import org.boomer.gameserver.repositories.RoomRepository;
import org.boomer.gameserver.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaTestRunner {

    private final RoomRepository roomRepository;

    public JpaTestRunner(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    public void testJpa() {
        // get user by username
//        List<RoomWaitingProjection> rooms = roomRepository.getRoomList();

//        for (RoomWaitingProjection r : rooms) {
//            System.out.println(
//                    "Room{id=" + r.getId() +
//                            ", code=" + r.getRoomCode() +
//                            ", ownerId=" + r.getOwnerId() +
//                            ", ownerUsername=" + r.getUsername() +
//                            ", status=" + r.getStatus() +
//                            ", maxPlayers=" + r.getMaxPlayers() +
//                            ", playerCount=" + r.getPlayerCount() +
//                            ", createdAt=" + r.getCreatedAt() +
//                            "}"
//            );
//        }
    }
}
