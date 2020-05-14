package lu.lllc.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import lu.lllc.entities.Tweet;
import lu.lllc.entities.User;

public interface TweetRepository extends JpaRepository<Tweet, Integer> {

	List<Tweet> findAllByUser(User user);

	@Query("SELECT t FROM Tweet t WHERE t.title LIKE :start% ORDER BY created DESC")
	List<Tweet> findAllByStart(@Param("start") String start);
 
	List<Tweet> findTweetsByTitleIgnoreCaseContainsOrTweetTextIgnoreCaseContainsOrUserFirstNameIgnoreCaseContainsOrUserLastNameIgnoreCaseContainsOrUserEmailIgnoreCaseContains(String t, String tt,String fn,String ln,String e, Pageable pageable);
	
	List<Tweet> findTweetsByTitleIgnoreCaseContainsOrTweetTextIgnoreCaseContainsOrUserFirstNameIgnoreCaseContainsOrUserLastNameIgnoreCaseContainsOrUserEmailIgnoreCaseContains(String t, String tt,String fn,String ln,String e);

}
