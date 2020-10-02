package ru.apolyakov.social_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.apolyakov.social_network.model.WallPost;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface WallPostRepository  extends JpaRepository<WallPost, Long> {
    @Query(value= "SELECT distinct wp.* FROM wall_post wp " +
            " INNER JOIN user_subscription us ON wp.to_user_id=us.friend_id OR wp.to_user_id=us.user_id" +
            " WHERE (us.friend_id = ?1 OR us.user_id=?1) AND wp.id >= ?2 " +
            " ORDER BY wp.date_created DESC " +
            " LIMIT ?3",
            nativeQuery = true)
    List<WallPost> getByToUserInWithOffset(Long userId, Long minId, Long limit);


    List<WallPost> getWallPostsByToUserOrderByDateCreatedDesc(Long toUser);
}
