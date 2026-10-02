package com.cineShare.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class ListaIndicacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToMany
	@JoinTable(
			name = "lista_titulos",
			joinColumns = @JoinColumn(name = "lista_id"),
			inverseJoinColumns = @JoinColumn(name = "titulos_id")
			)
	private List<Titulo> titulos;
}
