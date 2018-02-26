package com.example.user.omdbparser;

//Film bilgilerinin bulundugu siniftir.
public class InformationAboutFilm {

    private String title;
    private int year;
    private String poster;

    public InformationAboutFilm(String title,int year,String poster)
    {
        this.title = title;
        this.year = year;
        this.poster = poster;
    }

    public String getTitle()
    {
        return title;
    }

    public int getYear()
    {
        return year;
    }

    public String getPoster() {return poster;}
}