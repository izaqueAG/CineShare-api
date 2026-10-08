package com.cineShare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cineShare.model.ListaIndicacao;

public interface ListaIndicacaoRepository extends JpaRepository<ListaIndicacao, Long>{
	
	@Override
	default List<ListaIndicacao> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	default <S extends ListaIndicacao> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	default Optional<ListaIndicacao> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
