package lu.lllc.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lu.lllc.entities.User;
import lu.lllc.repositories.LikeRepository;
import lu.lllc.repositories.UserRepository;

@Controller
public class MainController {

	@Autowired
	private HttpSession session;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LikeRepository likeRepository;

	@RequestMapping("/")
	public String index(Model model, Principal principal) {
		if (principal != null) {
			if (session.getAttribute("userId") == null) {
				session.setAttribute("userId", userRepository.findByEmail(principal.getName()).getId());
				session.setAttribute("userFirstName", userRepository.findByEmail(principal.getName()).getFirstName());
				int userId = userRepository.findByEmail(principal.getName()).getId();
				model.addAttribute("userId", userId);
			}
		}

		return "index";
	}
	
	/* Below code is just a proof that SQL is readable for SpringBoot functionalities, shows users that like tweet with id="1" */
	@ModelAttribute("likingUsers")
	public List<User> getLikers() {
		return this.userRepository.getLikers("1",PageRequest.of(0, 5));
	}
}
