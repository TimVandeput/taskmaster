package be.taskmaster.feed_service.adapters.web;

import be.taskmaster.feed_service.domain.feed.FeedRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedRepository repo;

    public FeedController(FeedRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Object byTeam(@RequestParam String teamId) {
        return repo.findByTeamIdOrderByCreatedAtDesc(teamId);
    }
}
