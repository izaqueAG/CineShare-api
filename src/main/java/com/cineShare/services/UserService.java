package com.cineShare.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.cineShare.repository.UserRepository;
import com.cineShare.model.*;

@Service
public class UserService {
	
//	aplica as regras de negocio
	
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
	
	public List<User> listAll(){
		
		return userRepository.findAll();
	}
	
	public User salvar(User user) {

	    if (userRepository.existsByEmail(user.getEmail())) {
	        throw new IllegalArgumentException("E-mail já cadastrado");
	    }

	    return userRepository.save(user);
	}
	
	public List<User> findByNameOrId(String conteudo){

		    if (conteudo.matches("\\d+")) {

		        Long id = Long.parseLong(conteudo);

		        return userRepository.findById(id)
		                .map(user -> List.of(user))
		                .orElse(List.of());

		    } else {

		        return userRepository
		                .findByNomeContainingIgnoreCase(conteudo);
		    }
		}
	}

