package com.cineShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cineShare.model.Titulo;
import java.util.List;
import java.time.LocalDate;


public interface TituloRepository extends JpaRepository<Titulo, Long> {
	
	List<Titulo> findByAnoLancamento(String nome);

}