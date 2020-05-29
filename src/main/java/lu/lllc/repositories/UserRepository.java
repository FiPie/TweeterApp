package lu.lllc.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lu.lllc.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	User findByEmail(String email);
	
	@Query(value = "SELECT * FROM users u LEFT JOIN likes l ON u.id=l.user_id WHERE l.tweet_id=:tweetid AND l.value=1 ORDER BY l.created ASC", nativeQuery = true)
	List<User> getLikers(@Param("tweetid") String tweetid, Pageable pageable);
	
}
