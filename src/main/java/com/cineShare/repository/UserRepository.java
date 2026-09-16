package com.cineShare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cineShare.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
//	implementa os metodos em JpaRepository para persistir os dados no banco de dados
	
	List<User> findByNomeContainingIgnoreCase(String nome);
	
//	@Override
//	default Optional<User> findById(Long id) {
//		// TODO Auto-generated method stub
//		return Optional.empty();
//	}
	
	boolean existsByEmail(String string);

}
