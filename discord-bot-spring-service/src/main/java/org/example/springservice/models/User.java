package org.example.springservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 50)
    private Long ID;

    @Column(unique = true, nullable = false)
    private UUID minecraftUUID;

    @Column(unique = true, nullable = false)
    private String username;

    private boolean onlineStatus;
    private int achievementsCompleted;
    private Date firstSeen;
    private Date lastOnline;

    public User(UUID minecraftUUID, String username) {
        this.minecraftUUID = minecraftUUID;
        this.username = username;
        this.onlineStatus = true;
        this.achievementsCompleted = 0;
        this.firstSeen = new Timestamp(new Date().getTime());
        this.lastOnline = new Timestamp(new Date().getTime());
    }

    public void increaseAchievementsCompleted() {
        this.achievementsCompleted++;
    }
}
