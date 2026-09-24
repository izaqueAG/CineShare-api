package com.cineShare.services;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import DTO.TmdbResposta;
import DTO.TmdbResultado;
import tools.jackson.databind.ObjectMapper;

import com.cineShare.model.Titulo;
import com.cineShare.repository.TituloRepository;

@Service
public class TituloService {

    private final ObjectMapper mapper = new ObjectMapper();
    
    private final TituloRepository tituloRepository;

    public TituloService(TituloRepository tituloRepository) {
        this.tituloRepository = tituloRepository;
    }
    
    @Value("${tmdb.token}")  
    private String token;

    public Titulo salvar(Titulo titulo) {
        return tituloRepository.save(titulo);
    }

    public Titulo getTitulo(String nomeTitulo) {

        try {
            String tituloCodificado = URLEncoder.encode(
                    nomeTitulo,
                    StandardCharsets.UTF_8
            );
            

            String url = "https://api.themoviedb.org/3/search/movie"
                    + "?query=" + tituloCodificado
                    + "&language=pt-BR";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Erro na API TMDB: " + response.statusCode()
                );
            }

            TmdbResposta resposta = mapper.readValue(
                    response.body(),
                    TmdbResposta.class
            );

            if (resposta.getResults() == null
                    || resposta.getResults().isEmpty()) {

                throw new RuntimeException(
                        "Título não encontrado: " + nomeTitulo
                );
            }

            TmdbResultado resultado = resposta.getResults().get(0);
//            verificar se titulo exist na tabela titulos, caso nao, cria um novo objeto
            
            List<Titulo> tituloExistente = tituloRepository.findByNome(resultado.getTitle());

            if (!tituloExistente.isEmpty()) {
                return tituloExistente.get(0);
            }

            Titulo titulo = new Titulo();

            titulo.setNome(resultado.getTitle());

            if (resultado.getReleaseDate() != null
                    && !resultado.getReleaseDate().isBlank()) {

                titulo.setAnoLancamento(
                        LocalDate.parse(resultado.getReleaseDate())
                );
            }

            titulo.setPlataforma("Em desenvolvimento");
            

            
//            persiste titulo em dbTitulo
            return tituloRepository.save( titulo);
          

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao consultar título: " + e.getMessage(),
                    e
            );
        }
    }

	
    public List<Titulo> findAll() {
		// TODO Auto-generated method stub
		return tituloRepository.findAll();
	}
}