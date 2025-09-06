package org.example.springservice.repository;

import org.example.springservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMinecraftUUID(UUID minecraftUUID);

    Optional<User> findByUsername(String username);

    List<User> findByOnlineStatusTrue();

    @Query(value = "SELECT * FROM users ORDER BY achievements_completed DESC LIMIT 5", nativeQuery = true)
    List<User> findTop5ByOrderByAchievementsCompleted();
}
