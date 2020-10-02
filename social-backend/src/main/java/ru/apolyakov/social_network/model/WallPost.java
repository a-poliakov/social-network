package ru.apolyakov.social_network.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Table(name = "wall_post")
public class WallPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id")
    private Long fromUser;

    @Column(name = "to_user_id")
    private Long toUser;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "post_body")
    private String body;
}
