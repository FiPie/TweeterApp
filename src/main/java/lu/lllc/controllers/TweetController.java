package lu.lllc.controllers;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import lu.lllc.entities.Like;
import lu.lllc.entities.Tweet;
import lu.lllc.entities.User;
import lu.lllc.repositories.ImageRepository;
import lu.lllc.repositories.LikeRepository;
import lu.lllc.repositories.TweetRepository;
import lu.lllc.repositories.UserRepository;
import lu.lllc.entities.Image;

@Controller
@RequestMapping("/tweet")
public class TweetController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TweetRepository tweetRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private HttpSession session;
	

	@GetMapping("/add")
	public String getForm(Model model) {
		Tweet tweet = new Tweet();
		Image image = new Image();
		tweet.setImage(image);
		model.addAttribute("tweet", tweet);
		return "tweet/addtweet";
	}

	@PostMapping("/add")
	public String postForm(@Validated @ModelAttribute("tweet") Tweet tweet, BindingResult bindingResult,
			@RequestParam("file") MultipartFile file, Principal principal) {
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
			
			String mimetype = file.getContentType();
			String type = mimetype.split("/")[0];
			
			if (type.equals("image")) {
				Image image = new Image();
				image.setTweet(tweet);
				image.setMime(mimetype);
				try {
					image.setImage(file.getBytes());
					tweet.setImage(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			this.tweetRepository.save(tweet);
			return "redirect:/tweet/list";
		}
	}

	@RequestMapping("/list")
	public String getList(@ModelAttribute("search") String search, @ModelAttribute("size") String size,
			@ModelAttribute("sort") String sort, @ModelAttribute("page") String page, Model model) {

		Integer pageNo, pageSize, totalNoTweets, lastPage;

		String sortBy = sort.isBlank() ? (session.getAttribute("sort") != null ? (String) session.getAttribute("sort") : "created")  : sort;
		String searchQuery = search.isBlank() ? (session.getAttribute("search") != null ? (String) session.getAttribute("search") : "") : search;

		totalNoTweets = searchQuery == "" ? tweetRepository.findAll().size()
				: tweetRepository
						.findTweetsByTitleIgnoreCaseContainsOrTweetTextIgnoreCaseContainsOrUserFirstNameIgnoreCaseContainsOrUserLastNameIgnoreCaseContainsOrUserEmailIgnoreCaseContains(
								searchQuery, searchQuery, searchQuery, searchQuery, searchQuery)
						.size();

		pageSize = size.isBlank() ? (session.getAttribute("size") != null ? (int) session.getAttribute("size") : 5) 
				: Integer.valueOf(size);
		lastPage = totalNoTweets == 0 ? (session.getAttribute("page") != null ? (int) session.getAttribute("page") : 0)
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

		session.removeAttribute("search");
		session.removeAttribute("size");
		session.removeAttribute("sort");
		session.removeAttribute("page");
		
		model.addAttribute("search", searchQuery);
		model.addAttribute("size", pageSize);
		model.addAttribute("sort", sortBy);
		model.addAttribute("page", pageNo);
		model.addAttribute("availableTweets", availableTweets);
		model.addAttribute("totalTweets", totalNoTweets);

		return "tweet/list";
	}

	//TODO
	@GetMapping("/getImage/{id}")
	ResponseEntity<byte[]> getImage(@PathVariable("id") int id){
		
		HttpHeaders headers = new HttpHeaders();
	    Optional<Image> image_op = imageRepository.findById(id);
	    if(image_op.isPresent()) {
	    	Image image = image_op.get();
	    	//In this case it would be better to store the image as a BLOB
	    	//Because we decided to store it in Base64 encoded form - we need to decode it now
	    	//You've just learned how to do it
	    	
	    	byte[] data = image.getImage();
	    	
		    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		    headers.setContentType(MediaType.parseMediaType(image.getMime()));
		     
		    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(data, headers, HttpStatus.OK);
		    return responseEntity;
	    	
	    }
	    
	    return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		
	}
	//TODO
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
	
	@PostMapping("/like")
	public String saveLike(
			@ModelAttribute("search") String search, 
			@ModelAttribute("size") String size,
			@ModelAttribute("sort") String sort, 
			@ModelAttribute("page") String page,
			@ModelAttribute("tweetId") String tweetId,
			@ModelAttribute("value") String value,
			Principal principal, Model model) {
		
		Like like = new Like();
		int tweetID = Integer.valueOf(tweetId);
		like.setTweet(tweetRepository.getOne(tweetID));
		if (principal != null) {
			int userId = userRepository.findByEmail(principal.getName()).getId();
			int likeValue = Integer.valueOf(value);
			if (likeRepository.likeExists(userId, tweetID, likeValue) < 1) {
				like.setUser(userRepository.getOne(userId));
				like.setValue(likeValue);
				this.likeRepository.save(like);
			}
			session.setAttribute("search", search);
			session.setAttribute("size", Integer.valueOf(size));
			session.setAttribute("sort", sort);
			session.setAttribute("page", Integer.valueOf(page));
				
			return "redirect:/tweet/list#tweetHeader" + tweetId;
		}
		
		return "redirect:/login";
	}
	//TODO
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
