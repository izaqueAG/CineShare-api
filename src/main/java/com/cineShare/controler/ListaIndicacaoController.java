package com.cineShare.controler;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.services.ListaIndicacaoService;

import DTO.ListaIndicacaoDTO;

@RestController
@RequestMapping("/listas")
public class ListaIndicacaoController {
	
	private final ListaIndicacaoService listaIndicacaoService;
	
	public  ListaIndicacaoController(ListaIndicacaoService listaIndicacaoService) {
		this.listaIndicacaoService = listaIndicacaoService;
	}
	
	@PostMapping
	public ListaIndicacao criarLista(@RequestBody ListaIndicacaoDTO dto) {
		
		return listaIndicacaoService.criarLista(dto);
	}
	
	
}
