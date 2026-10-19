package com.cineShare.controler;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.model.User;
import com.cineShare.services.ListaIndicacaoService;

import DTO.ListaIndicacaoDTO;

@RestController
@RequestMapping("/listas")
public class ListaIndicacaoController {
	
	private final ListaIndicacaoService listaIndicacaoService;
	
	public  ListaIndicacaoController(ListaIndicacaoService listaIndicacaoService) {
		this.listaIndicacaoService = listaIndicacaoService;
	}
	
	@PostMapping("/create")
	public ListaIndicacao criarLista(@RequestBody ListaIndicacaoDTO dto) {
		
		return listaIndicacaoService.criarLista(dto);
	}
	
	@GetMapping()
	public List<ListaIndicacao> buscar(){
		return listaIndicacaoService.listAll();
	}
	
	
}
