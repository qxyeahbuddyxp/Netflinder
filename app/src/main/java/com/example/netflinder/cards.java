package com.example.netflinder;

public class cards {
    private String movieId;
    private String movieTitle;
    private String movieGenre;
    private String moviePoster;
    public cards (String movieId, String movieTitle, String moviePoster, String movieGenre){//,String movieGenre
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.movieGenre = movieGenre;
    }

    public String getMovieId(){
        return movieId;
    }
    public void setMovieId(String movieId){
        this.movieId = movieTitle;
    }

    public String getTitle(){
        return movieTitle;
    }

    public void setTitle(String movieTitle){
        this.movieTitle = movieTitle;
    }

    public String getMoviePoster(){
        return moviePoster;
    }
    public void setMoviePoster(String moviePoster){
        this.moviePoster = moviePoster;
    }

    public void setMovieGenre(String movieGenre){
        this.movieGenre = movieGenre;
    }
    public String getMovieGenre(){
        return movieGenre;
    }
}
