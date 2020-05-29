package lu.lllc.controllers;

import java.security.Principal;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lu.lllc.repositories.UserRepository;

@Controller
public class MainController {

	@Autowired
	private HttpSession session;
	@Autowired
	private UserRepository userRepository;

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
}