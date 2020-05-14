package lu.lllc.controllers;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lu.lllc.entities.Like;
import lu.lllc.entities.Tweet;
import lu.lllc.entities.User;
import lu.lllc.repositories.LikeRepository;
import lu.lllc.repositories.TweetRepository;
import lu.lllc.repositories.UserRepository;

@Controller
@RequestMapping("/tweet")
public class TweetController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TweetRepository tweetRepository;
	@Autowired
	private LikeRepository likeRepository;

	@GetMapping("/add")
	public String getForm(Model model) {
		model.addAttribute("tweet", new Tweet());
		return "tweet/addtweet";
	}

	@PostMapping("/add")
	public String postForm(@Validated @ModelAttribute("tweet") Tweet tweet, BindingResult bindingResult,
			Principal principal) {
		if (bindingResult.hasErrors()) {
			return "tweet/addtweet";
		} else {
			/*
			 * When we have a hidden field and we want to assign it a value, strangely
			 * thymeleaf will ignore the th:value property Hence we have to set tweet.user
			 * property here... there are some cases where thymeleaf does not work properly.
			 */
			User author = userRepository.findByEmail(principal.getName());
			tweet.setUser(author);
			this.tweetRepository.save(tweet);
			return "redirect:/tweet/list";
		}
	}

	@RequestMapping("/list")
	public String getList(@ModelAttribute("search") String search, @ModelAttribute("size") String size,
			@ModelAttribute("sort") String sort, @ModelAttribute("page") String page, Model model) {

		Integer pageNo, pageSize, totalNoTweets, lastPage;

		String sortBy = sort.isBlank() ? "created" : sort;
		String searchQuery = search.isBlank() ? "" : search;

		totalNoTweets = searchQuery == "" ? tweetRepository.findAll().size()
				: tweetRepository
						.findTweetsByTitleIgnoreCaseContainsOrTweetTextIgnoreCaseContainsOrUserFirstNameIgnoreCaseContainsOrUserLastNameIgnoreCaseContainsOrUserEmailIgnoreCaseContains(
								searchQuery, searchQuery, searchQuery, searchQuery, searchQuery)
						.size();

		pageSize = size.isBlank() ? 5 : Integer.valueOf(size);
		lastPage = totalNoTweets == 0 ? 0
				: (totalNoTweets % pageSize == 0) ? (totalNoTweets / pageSize) - 1 : (totalNoTweets / pageSize);

		pageNo = page.isBlank() ? 0
				: Integer.valueOf(page) < 0 ? 0 : Integer.valueOf(page) >= lastPage ? lastPage : Integer.valueOf(page);

		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		List<Tweet> availableTweets = tweetRepository
				.findTweetsByTitleIgnoreCaseContainsOrTweetTextIgnoreCaseContainsOrUserFirstNameIgnoreCaseContainsOrUserLastNameIgnoreCaseContainsOrUserEmailIgnoreCaseContains(
						searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, pageable);

		availableTweets.forEach(tweet -> {
			tweet.setLikesNo((int) likeRepository.likesByTweet(tweet.getId()));
			tweet.setDislikesNo((int) likeRepository.dislikesByTweet(tweet.getId()));
		});

		model.addAttribute("search", searchQuery);
		model.addAttribute("size", pageSize);
		model.addAttribute("sort", sortBy);
		model.addAttribute("page", pageNo);
		model.addAttribute("availableTweets", availableTweets);
		model.addAttribute("totalTweets", totalNoTweets);

		return "tweet/list";
	}

	@RequestMapping("/{tweetId}/like")
	public String likeThisTweet(@PathVariable int tweetId, @RequestParam Optional<String> user, Principal principal) {
		Like like = new Like();
		like.setTweet(tweetRepository.getOne(tweetId));
		if (principal != null) {
			int userId = userRepository.findByEmail(principal.getName()).getId();
			int value = 1;
			if (likeRepository.likeExists(userId, tweetId, value) < 1) {
				like.setUser(userRepository.getOne(userId));
				like.setValue(value);
				this.likeRepository.save(like);
			}
			if (user.isPresent()) {
				return "redirect:/user/" + user.get() + "/tweets#tweetHeader" + tweetId;
			} else {
				return "redirect:/tweet/list#tweetHeader" + tweetId;
			}
		}
		return "redirect:/login";
	}

	@RequestMapping("/{tweetId}/dislike")
	public String dislikeThisTweet(@PathVariable int tweetId, @RequestParam Optional<String> user,
			Principal principal) {
		Like dislike = new Like();
		dislike.setTweet(tweetRepository.getOne(tweetId));
		if (principal != null) {
			int userId = userRepository.findByEmail(principal.getName()).getId();
			int value = -1;
			if (likeRepository.likeExists(userId, tweetId, value) < 1) {
				dislike.setUser(userRepository.getOne(userId));
				dislike.setValue(value);
				this.likeRepository.save(dislike);
			}
			if (user.isPresent()) {
				return "redirect:/user/" + user.get() + "/tweets#tweetHeader" + tweetId;
			} else {
				return "redirect:/tweet/list#tweetHeader" + tweetId;
			}
		}
		return "redirect:/login";
	}

	@ModelAttribute("availableTweets")
	public List<Tweet> getAllTweets() {
		List<Tweet> availableTweets = this.tweetRepository.findAll();
		availableTweets.forEach(tweet -> {
			tweet.setLikesNo((int) likeRepository.likesByTweet(tweet.getId()));
			tweet.setDislikesNo((int) likeRepository.dislikesByTweet(tweet.getId()));
		});
		return availableTweets;
	}

	@ModelAttribute("availableUsers")
	public List<User> getAllUsers() {
		return this.userRepository.findAll();
	}
}

/*
 * import java.time.Duration; import java.time.Instant; Instant start =
 * Instant.now(); //your code Instant end = Instant.now(); Duration timeElapsed
 * = Duration.between(start, end); System.out.println("Time taken: "+
 * timeElapsed.toMillis() +" milliseconds");
 */
