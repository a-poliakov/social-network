package ru.apolyakov.social_network.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Table(name = "wall_post")
public class WallPost {
}
