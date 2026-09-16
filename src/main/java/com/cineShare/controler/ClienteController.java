package com.cineShare.controler;


import java.nio.file.attribute.UserPrincipalLookupService;
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

import com.cineShare.model.Cliente;
import com.cineShare.repository.ClienteRepository;

@RestController
@RequestMapping
public class ClienteController {
	
	@Autowired
	private ClienteRepository clienteRepository; 
	
//	@GetMapping("/clientes")
//	public List<Cliente> listar() {
//		return clienteRepository.findAll();
//	}
//	
	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente adicionar(@RequestBody Cliente cliente) {
		return clienteRepository.save(cliente);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable Long id) {
		
		userService.show(id);

        return new ResponseEntity<>(HttpStatus.OK);
	}
}

