package lu.lllc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lu.lllc.dto.LoginDto;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String prepareLoginForm(Model model) {
    	model.addAttribute( "loginDto", new LoginDto() );
        return "login";
    }
}
