package com.cineShare.repository;

import java.util.List;

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

}
