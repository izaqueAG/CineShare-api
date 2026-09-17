package com.cineShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cineShare.model.Titulo;

public interface TituloRepository extends JpaRepository<Titulo, Long> {

}