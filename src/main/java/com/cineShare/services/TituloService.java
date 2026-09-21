package com.cineShare.services;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import DTO.TmdbResposta;
import DTO.TmdbResultado;
import tools.jackson.databind.ObjectMapper;

import com.cineShare.model.Titulo;

@Service
public class TituloService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1NjdjYzZjNGEwZjFmNjA0OTVmNGViNzNhYTQ5NjM2NiIsIm5iZiI6MTc4OTU5OTU0Ny4zNTM5OTk5LCJzdWIiOiI2YWFiMWYzYjBjODVkMjE2NzgwMTc4NzQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.zy92NL7FZq040-6mx_FeSFUMoP7O50MAAIyMgsRiWaU";

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

            Titulo titulo = new Titulo();

            titulo.setNome(resultado.getTitle());

            if (resultado.getReleaseDate() != null
                    && !resultado.getReleaseDate().isBlank()) {

                titulo.setAnoLancamento(
                        LocalDate.parse(resultado.getReleaseDate())
                );
            }

            titulo.setPlataforma("Não informada");

            return titulo;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao consultar título: " + e.getMessage(),
                    e
            );
        }
    }
}