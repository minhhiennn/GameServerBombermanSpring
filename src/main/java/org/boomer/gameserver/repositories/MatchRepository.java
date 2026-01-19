package org.boomer.gameserver.repositories;

import org.boomer.gameserver.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {

}
