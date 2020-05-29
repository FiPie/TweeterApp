package lu.lllc.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
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
import org.springframework.transaction.annotation.Transactional;
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

import lu.lllc.entities.Image;
import lu.lllc.entities.Like;
import lu.lllc.entities.Tweet;
import lu.lllc.entities.User;
import lu.lllc.repositories.ImageRepository;
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
			return "redirect:/tweet/add";
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
	
	
	
	@PostMapping("/delete")
	public String removeTweet(@ModelAttribute("tweetId") int tweetId, @ModelAttribute("userId") int userId, Principal principal) {
		
		if (userRepository.findByEmail(principal.getName()).getId() == userId
				|| userRepository.findByEmail(principal.getName()).getUserRoles().stream()
						.anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) {
			
			Tweet tweet;
			Optional<Tweet> optionalTweet = this.tweetRepository.findById(tweetId);
			/*
			 * User user; Optional<User> optionalUser =
			 * this.userRepository.findById(userId); Image image; Optional<Image>
			 * optionalImage = this.imageRepository.findById(tweetId);
			 */
			
			if (!optionalTweet.isEmpty() /* && !optionalUser.isEmpty() */) {
				tweet = optionalTweet.get();
				
				/*
				 * user = optionalUser.get(); tweet.setUser(user); if(!optionalImage.isEmpty())
				 * { image = optionalImage.get(); tweet.setImage(image); }
				 */
				System.out.println("");
				System.out.println("@ModelAttribute(\"tweetId\") int tweetId="+ tweetId +"");
				System.out.println("attempting to DELETE tweet.getId()="+ tweet.getId() +"");
				/* this.tweetRepository.delete(tweetRepository.getOne(tweetId)); */
				tweetRepository.flush();
				this.tweetRepository.delete(tweet);
				
				/* tweetRepository.deleteById(tweetId); */
				System.out.println("DELETED ? : " + !tweetRepository.existsById(tweetId));
				return "redirect:/user/"+userId+"/tweets";
			}else {
				return "redirect:/tweet/showTweet/"+tweetId+"";
			}
		} else {
			return "redirect:/tweet/showTweet/"+ tweetId+"";
		}
	}
	
	
	
	@PostMapping("/edit/form")
	public String getTweetEditForm(@ModelAttribute("tweetId") int tweetId, @ModelAttribute("userId") int userId, Model model, Principal principal) {
		
		Tweet tweet = tweetRepository.getOne(tweetId);
		User tweetAuthor = userRepository.getOne(userId);
		model.addAttribute("tweet",tweet);
		model.addAttribute("user",tweetAuthor);
		
		return "tweet/edittweet";
	}
	
	@PostMapping("/edit")
	@Transactional
	public String editTweet(@Validated @ModelAttribute("tweet") Tweet tweet, BindingResult bindingResult) {
		/* User author = userRepository.getOne(tweet.getUser().getId()); */
		if (bindingResult.hasErrors()) {
			return "redirect:/tweet/showTweet/"+tweet.getId()+"";
		} else {
			this.tweetRepository.save(tweet);
			return "redirect:/tweet/showTweet/"+tweet.getId()+"";
		}
	}
	

	@RequestMapping("/list")
	public String getList(@ModelAttribute("search") String search, @ModelAttribute("size") String size,
			@ModelAttribute("sort") String sort, @ModelAttribute("page") String page, @ModelAttribute("order") String order,
			Model model) {

		Integer pageNo, pageSize, totalNoTweets, lastPage, orderSort;

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
		
		orderSort = order.isBlank() ? (session.getAttribute("order") == null ? -1 : (int)session.getAttribute("order"))
				: Integer.valueOf(order);
				
		
		Pageable pageable = (orderSort == 1) 
				? PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending()) 
				: PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

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
		session.removeAttribute("order");
		
		model.addAttribute("search", searchQuery);
		model.addAttribute("size", pageSize);
		model.addAttribute("sort", sortBy);
		model.addAttribute("page", pageNo);
		model.addAttribute("order", orderSort);
		model.addAttribute("availableTweets", availableTweets);
		model.addAttribute("totalTweets", totalNoTweets);

		return "tweet/list";
	}
	
	
	@GetMapping("/showTweet/{id}")
	public String showOneTweet(@PathVariable("id") int id, Model model) {
	
		Tweet tweet = tweetRepository.getOne(id);
		User user = userRepository.getOne(tweet.getUser().getId());
		model.addAttribute("tweet", tweet);
		model.addAttribute("user", user);
		model.addAttribute("tweetsTotal", user.getTweets().size());
		
		return "tweet/showTweet";
	}

	
	@GetMapping("/getImage/{id}")
	ResponseEntity<byte[]> getImage(@PathVariable("id") int id){
		
		HttpHeaders headers = new HttpHeaders();
	    Optional<Image> image_op = imageRepository.findById(id);
	    if(image_op.isPresent()) {
	    	Image image = image_op.get(); 
	    	
	    	byte[] data = image.getImage();
	    	
		    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		    headers.setContentType(MediaType.parseMediaType(image.getMime()));
		     
		    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(data, headers, HttpStatus.OK);
		    return responseEntity;
	    	
	    }
	    
	    return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		
	}
	
//	I went with Thymeleaf utility class #list.size(myList) besides I didn't find any way to request and display ResposeEntity<String> as a th:text

//	@GetMapping("/getTweetTotalByUserId/{id}")
//	ResponseEntity<String> getNumberOfTweets(@PathVariable("id") int id){
//		
//		HttpHeaders headers = new HttpHeaders();
//		String tweetTotal = String.valueOf(userRepository.getOne(id).getTweets().size());
//	    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
//	    headers.setContentType(MediaType.parseMediaType("text/html")); 
//	    ResponseEntity<String> responseEntity = new ResponseEntity<>(tweetTotal, headers, HttpStatus.OK);
//	    return responseEntity;
//	}
	
	
	@PostMapping("/like")
	public String saveLike(
			@ModelAttribute("search") String search, 
			@ModelAttribute("size") String size,
			@ModelAttribute("sort") String sort, 
			@ModelAttribute("page") String page,
			@ModelAttribute("order") String order,
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
			session.setAttribute("order", Integer.valueOf(order));
			
			return "redirect:/tweet/list#tweetHeader" + tweetId;
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
