package com.example.netflinder;

public class MatchesObject {

    private String movieTitle, movieGenre, movieURL;

    public MatchesObject (String movieTitle, String movieGenre, String movieURL){
        this.movieTitle = movieTitle;
        this.movieGenre = movieGenre;
        this.movieURL = movieURL;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public String getMovieGenre() {
        return movieGenre;
    }
    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }
    public String getMovieURL() {
        return movieURL;
    }
    public void setMovieURL(String movieURL) {
        this.movieURL = movieURL;
    }

}
