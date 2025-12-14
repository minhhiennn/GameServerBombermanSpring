package org.boomer.gameserver.repositories;

import org.boomer.gameserver.entities.Room;
import org.boomer.gameserver.projection.RoomWaitingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query(value = """
                                      SELECT\s
                                           r.id,
                                           r.room_code,
                                           r.owner_id,
                                           u.username,
                                           r.status,
                                           r.max_players,
                                           r.created_at,
                                           COUNT(rp.user_id) AS player_count
                                       FROM rooms r
                                       LEFT JOIN room_players rp\s
                                           ON r.id = rp.room_id
                                       LEFT JOIN users u
                                           ON r.owner_id = u.id\s
                                       WHERE r.status = 'WAITING'
                                       GROUP BY\s
                                           r.id, r.room_code, r.owner_id,\s
                                           r.status, r.max_players, r.created_at
                                       ORDER BY r.created_at DESC;
            """, nativeQuery = true)
    List<RoomWaitingProjection> getRoomList();

    Optional<Room> findByRoomCode(String roomCode);
}
