package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/level-feedback")
public class LevelFeedbackController {
    private final LevelFeedbackRepository levelFeedbackRepository;

    public LevelFeedbackController(LevelFeedbackRepository levelFeedbackRepository) {
        this.levelFeedbackRepository = levelFeedbackRepository;
    }

    @GetMapping
    public Map<String, LevelFeedbackSummary> getLevelFeedback(FeedbackQuery query) {
        List<String> levelKeys = query.levelKeys != null ? query.levelKeys : List.of();
        String playerId = query.playerId != null ? query.playerId.trim() : "";

        List<LevelFeedback> feedbackRows = levelKeys.isEmpty()
            ? List.of()
            : levelFeedbackRepository.findByLevelKeyIn(levelKeys);

        Map<String, List<LevelFeedback>> grouped = new HashMap<>();
        for (LevelFeedback row : feedbackRows) {
            grouped.computeIfAbsent(row.getLevelKey(), ignored -> new ArrayList<>()).add(row);
        }

        Map<String, LevelFeedbackSummary> result = new HashMap<>();
        for (String levelKey : levelKeys) {
            List<LevelFeedback> rows = grouped.getOrDefault(levelKey, List.of());
            result.put(levelKey, toSummary(rows, playerId));
        }
        return result;
    }

    @PutMapping("/{levelKey}/rating")
    public LevelFeedbackSummary updateRating(@PathVariable String levelKey, @RequestBody RatingRequest request) {
        String playerId = sanitizePlayerId(request.playerId);
        int rating = request.rating;
        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5.");
        }

        LevelFeedback row = levelFeedbackRepository.findByLevelKeyAndPlayerId(levelKey, playerId)
            .orElseGet(LevelFeedback::new);
        if (row.getId() == null) {
            row.setLevelKey(levelKey);
            row.setPlayerId(playerId);
        }
        row.setRating(rating);
        row.setUpdatedAt(LocalDateTime.now());
        levelFeedbackRepository.save(row);

        return toSummary(levelFeedbackRepository.findByLevelKey(levelKey), playerId);
    }

    @PutMapping("/{levelKey}/comment")
    public LevelFeedbackSummary updateComment(@PathVariable String levelKey, @RequestBody CommentRequest request) {
        String playerId = sanitizePlayerId(request.playerId);
        String comment = request.comment != null ? request.comment.trim() : "";
        if (comment.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment cannot be empty.");
        }

        Optional<LevelFeedback> existing = levelFeedbackRepository.findByLevelKeyAndPlayerId(levelKey, playerId);
        if (existing.isEmpty() || existing.get().getRating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submit a rating before leaving a comment.");
        }

        LevelFeedback row = existing.get();
        row.setComment(comment);
        row.setUpdatedAt(LocalDateTime.now());
        levelFeedbackRepository.save(row);

        return toSummary(levelFeedbackRepository.findByLevelKey(levelKey), playerId);
    }

    private static String sanitizePlayerId(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing playerId.");
        }
        return playerId.trim();
    }

    private static LevelFeedbackSummary toSummary(List<LevelFeedback> rows, String currentPlayerId) {
        int ratingCount = 0;
        int ratingTotal = 0;
        Integer playerRating = null;
        String playerComment = "";
        List<CommentDto> comments = new ArrayList<>();

        for (LevelFeedback row : rows) {
            if (row.getRating() != null) {
                ratingCount++;
                ratingTotal += row.getRating();
            }

            boolean isPlayer = !currentPlayerId.isEmpty() && currentPlayerId.equals(row.getPlayerId());
            if (isPlayer) {
                playerRating = row.getRating();
                if (row.getComment() != null) {
                    playerComment = row.getComment();
                }
            }

            if (row.getComment() != null && !row.getComment().isBlank()) {
                Integer rowRating = row.getRating();
                int commentRating = rowRating != null ? rowRating : 0;
                comments.add(new CommentDto(
                    isPlayer ? "You" : "Player",
                    commentRating,
                    row.getComment(),
                    row.getUpdatedAt(),
                    isPlayer
                ));
            }
        }

        comments.sort(Comparator.comparing(CommentDto::updatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        double average = ratingCount > 0 ? ((double) ratingTotal) / ratingCount : 0.0;

        return new LevelFeedbackSummary(average, ratingCount, playerRating, playerComment, comments);
    }

    public static class FeedbackQuery {
        public List<String> levelKeys;
        public String playerId;
    }

    public static class RatingRequest {
        public String playerId;
        public int rating;
    }

    public static class CommentRequest {
        public String playerId;
        public String comment;
    }

    public record CommentDto(String author, int rating, String comment, LocalDateTime updatedAt, boolean isPlayer) {}

    public record LevelFeedbackSummary(
        double average,
        int count,
        Integer playerRating,
        String playerComment,
        List<CommentDto> comments
    ) {}
}
