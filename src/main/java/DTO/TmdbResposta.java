package DTO;

import java.util.List;

public class TmdbResposta {

    private List<TmdbResultado> results;

    public List<TmdbResultado> getResults() {
        return results;
    }

    public void setResults(List<TmdbResultado> results) {
        this.results = results;
    }
}