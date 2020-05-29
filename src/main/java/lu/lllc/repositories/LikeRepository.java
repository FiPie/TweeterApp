package lu.lllc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lu.lllc.entities.Like;
import lu.lllc.entities.User;

public interface LikeRepository extends JpaRepository<Like, Integer>{

	List<Like> findAllByUser(User user);
	
	@Query(value = "SELECT COUNT(*) FROM `likes` WHERE `tweet_id`=:tweetId AND `value`='1'", nativeQuery = true)
	int likesByTweet(@Param("tweetId") Integer tweetId);
	
	@Query(value = "SELECT COUNT(*) FROM `likes` WHERE `tweet_id`=:tweetId AND `value`='-1'", nativeQuery = true)
	int dislikesByTweet(@Param("tweetId") Integer tweetId);
	
	@Query(value = "SELECT COUNT(*) FROM `likes` WHERE `user_id`=:userId AND `tweet_id`=:tweetId AND `value`=:value", nativeQuery = true)
	int likeExists(@Param("userId") Integer userId, @Param("tweetId") Integer tweetId, @Param("value") Integer value);
	

}