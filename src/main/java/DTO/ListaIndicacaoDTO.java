package DTO;

import java.util.List;

import lombok.Data;

@Data
public class ListaIndicacaoDTO {
	
	private String nome;
	private Long userId;
	private List<String> titulos;

}
