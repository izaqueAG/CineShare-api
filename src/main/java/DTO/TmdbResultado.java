package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TmdbResultado {

    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    private Long id;

    @JsonProperty("poster_path")
    private String posterPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
}
