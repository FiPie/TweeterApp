package lu.lllc.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.imageio.spi.RegisterableService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import lu.lllc.dto.RegistrationDto;
import lu.lllc.entities.Like;
import lu.lllc.entities.Tweet;
import lu.lllc.entities.User;
import lu.lllc.entities.UserRole;
import lu.lllc.repositories.LikeRepository;
import lu.lllc.repositories.TweetRepository;
import lu.lllc.repositories.UserRepository;
import lu.lllc.repositories.UserRoleRepository;
import lu.lllc.services.RegistrationService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TweetRepository tweetRepository;
	@Autowired
	private RegistrationService registrationService;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private LikeRepository likeRepository;

	@GetMapping("/{id}/tweets")
	public String findTweetsByUserId(@PathVariable int id, Model model) {
		User user = this.userRepository.getOne(id);
		List<Tweet> tweets = this.tweetRepository.findAllByUser(user);
		int tweetsTotal = tweets.size();
		model.addAttribute("tweets", tweets);
		model.addAttribute("user", user);
		model.addAttribute("tweetsTotal", tweetsTotal);
		return "user/usertweets";
	}

	@GetMapping("/search-tweets")
	public String getTweetsByStart(@RequestParam String start, Model model) {
		List<Tweet> tweets = this.tweetRepository.findAllByStart(start);
		model.addAttribute("tweets", tweets);
		return "user/search";
	}

	@GetMapping("/add")
	public String getUserForm(Model model, Principal principal) {
		if (principal == null) {
			model.addAttribute("userForm", new RegistrationDto());
			return "user/adduser";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/add")
	public String registerUser(@Validated @ModelAttribute("userForm") RegistrationDto userForm, BindingResult result) {
		if (result.hasErrors()) {
			return "user/adduser";
		}
		boolean success = registrationService.register(userForm);

		if (success) {
			return "redirect:/login";
		} else {
			result.rejectValue("email", null, "something went wrong, try again");
			return "redirect:/user/add";
		}
	}

	@GetMapping("/all")
	public String getAll() {
		return "user/allusers";
	}

	/*
	 * @RequestMapping("/delete/{id}") public String removeUser(@PathVariable int
	 * id, Principal principal) { if
	 * (userRepository.findByEmail(principal.getName()).getId() == id ||
	 * userRepository.findByEmail(principal.getName()).getUserRoles().stream()
	 * .anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) { User user;
	 * Optional<User> optional = this.userRepository.findById(id); if
	 * (!optional.isEmpty()) { user = optional.get();
	 * this.userRepository.delete(user);
	 * 
	 * if (userRepository.findByEmail(principal.getName()).getUserRoles().stream()
	 * .anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) { return
	 * "redirect:/user/all"; }
	 * 
	 * return "redirect:/logout"; } else { return "user/allusers"; } } else { return
	 * "user/allusers"; } }
	 */
	
	/*
	 * @GetMapping("/edit/{id}") public String editUserGet(Model
	 * model, @PathVariable int id, Principal principal) { if
	 * (userRepository.findByEmail(principal.getName()).getId() == id ||
	 * userRepository.findByEmail(principal.getName()).getUserRoles().stream()
	 * .anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) { User user;
	 * Optional<User> optional = this.userRepository.findById(id); if
	 * (!optional.isEmpty()) { user = optional.get(); model.addAttribute("user",
	 * user);
	 * 
	 * return "/user/edituser";
	 * 
	 * } else { return "user/allusers"; } } else return "user/allusers"; }
	 */

	@PostMapping("/delete")
	public String removeUser(@ModelAttribute("userId") int userId, Principal principal) {

		if (userRepository.findByEmail(principal.getName()).getId() == userId
				|| userRepository.findByEmail(principal.getName()).getUserRoles().stream()
						.anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) {
			User user;
			Optional<User> optional = this.userRepository.findById(userId);

			if (!optional.isEmpty()) {
				user = optional.get();
				this.userRepository.delete(user);
				
				System.out.println("principal.getName() : " + principal.getName());
				
				if (userRepository.findByEmail(principal.getName()) != null && userRepository.findByEmail(principal.getName()).getUserRoles()
						.stream().anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) {
					System.out.println("I'M HERE ln133");
					return "redirect:/user/all";
				}
				return "redirect:/logout";
			} else {
				return "user/allusers";
			}
		} else {
			return "user/allusers";
		}
	}

	@PostMapping("/edit/form")
	public String editUserGet(@ModelAttribute("userId") int userId, Model model, Principal principal) {
		if (userRepository.findByEmail(principal.getName()).getId() == userId
				|| userRepository.findByEmail(principal.getName()).getUserRoles().stream()
						.anyMatch(role -> role.getRole().equals("ROLE_ADMIN"))) {
			User user;
			Optional<User> optional = this.userRepository.findById(userId);
			if (!optional.isEmpty()) {
				user = optional.get();
				model.addAttribute("user", user);
				return "/user/edituser";
			} else {
				return "user/allusers";
			}
		} else
			return "user/allusers";
	}

	@PostMapping("/edit")
	@Transactional
	public String editUser(@Validated @ModelAttribute("user") User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "/user/edituser";
		} else {

			this.userRepository.save(user);
			return "redirect:/user/all";
		}

	}

	@PostMapping("/like")
	public String saveLike(@ModelAttribute("tweetId") String tweetId, @ModelAttribute("value") String value,
			@ModelAttribute("tweetAuthorId") String tweetAuthorId, Principal principal, Model model) {
		Like like = new Like();
		int tweetID = Integer.valueOf(tweetId);
		int userID = Integer.valueOf(tweetAuthorId);

		like.setTweet(tweetRepository.getOne(tweetID));
		if (principal != null) {
			int userId = userRepository.findByEmail(principal.getName()).getId();
			int likeValue = Integer.valueOf(value);
			if (likeRepository.likeExists(userId, tweetID, likeValue) < 1) {
				like.setUser(userRepository.getOne(userId));
				like.setValue(likeValue);
				this.likeRepository.save(like);
			}

			return "redirect:/user/" + userID + "/tweets#tweetHeader" + tweetId;
		}

		return "redirect:/login";
	}

	@ModelAttribute("availableUsers")
	public List<User> getAllUsers() {
		List<User> availableUsers = this.userRepository.findAll();
		return availableUsers;
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
}
