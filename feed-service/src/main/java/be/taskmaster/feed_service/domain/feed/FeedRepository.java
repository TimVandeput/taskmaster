package be.taskmaster.feed_service.domain.feed;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedRepository extends JpaRepository<FeedItem, Long> {
    List<FeedItem> findByTeamIdOrderByCreatedAtDesc(String teamId);
}
