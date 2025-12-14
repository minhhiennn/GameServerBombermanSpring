package org.boomer.gameserver.repositories;

import org.boomer.gameserver.entities.RoomPlayer;
import org.boomer.gameserver.entities.RoomPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, RoomPlayerId> {

    List<RoomPlayer> findByRoom_RoomCode(String roomCode);
    @Override
    void deleteById(RoomPlayerId roomPlayerId);

    void deleteByRoom_Id(Integer roomId);

    void deleteByRoom_IdAndUserId(Integer roomId, Integer userId);
}
