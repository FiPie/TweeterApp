package lu.lllc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.lllc.dto.LikersRequestDto;
import lu.lllc.entities.User;
import lu.lllc.repositories.UserRepository;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/likers")
	List<User> getLikers(@RequestBody LikersRequestDto request){
		
		List<User> likers = userRepository.getLikers(request.getTweetid(),
				PageRequest.of(0, request.getSize()));
		
		return likers;
	}
	
	@PostMapping("/ourJSON")
	String getOurJSON(@RequestBody LikersRequestDto request) {
		
		List<User> likers = userRepository.getLikers(request.getTweetid(),
				PageRequest.of(0, request.getSize()));
		
		ObjectMapper mapper = new ObjectMapper();

		String jString = "";
		try {
			jString = mapper.writeValueAsString(likers);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jString;
	}
	
}