package com.cineShare.controler;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cineShare.model.User;
import com.cineShare.repository.UserRepository;
import com.cineShare.services.UserService;

@RestController
public class UserController {
	
//	Recebe a requisição http e direciona para o UserService
	
	private final UserService userService;


	UserController(UserService userService) {
		this.userService = userService;
	} 
	
	
	@GetMapping("/user")
	public List<User> buscar(){
		return userService.listAll();
	}
	
	@PostMapping("/user")
	@ResponseStatus(HttpStatus.CREATED)
	public User salvar(@RequestBody User user) {
		return userService.salvar(user);
	}

	@GetMapping("/{buscar}")
	public List<User> findByName(@PathVariable String buscar) {
		
		return userService.findByNameOrId(buscar);
	}
	
//	@GetMapping("/{id}")
//	public List<User> findById(@PathVariable String id) {
//		
//
//		return userService.findByNameOrId(id);
//	}
}

