package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelFeedbackRepository extends JpaRepository<LevelFeedback, Long> {
    List<LevelFeedback> findByLevelKeyIn(List<String> levelKeys);
    List<LevelFeedback> findByLevelKey(String levelKey);
    Optional<LevelFeedback> findByLevelKeyAndPlayerId(String levelKey, String playerId);
}
