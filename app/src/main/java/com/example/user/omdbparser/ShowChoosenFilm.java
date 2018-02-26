package com.example.user.omdbparser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.user.omdbparser.Films.IMDB_ID;

public class ShowChoosenFilm extends Activity {

    private TextView titleC,releasedC,runtimeC,genreC,plotC,directorC,actorC,awardsC,imdbRatingC;
    private ImageView posterC;
    private RatingBar ratingBarC;
    private String urlName = "http://www.omdbapi.com/?apikey=852159f0&i=" , newUrl;
    private String imdbID;
    private Intent intent;
    private StringBuffer buffer;
    private String title,released,genre,director,actor,plot,awards,poster,runtime;
    private double imdbRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosenfilm);

        intent = this.getIntent();
        imdbID = intent.getStringExtra(IMDB_ID);
        createUrl();
        new JsonParse().execute(newUrl);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createScreen();
    }

    public void createUrl() {newUrl = urlName.concat(imdbID);}

    //Gerekli bilgiler internet uzerinden baglanti saglanarak elde ediliyor ve kullaniciya gostermek icin saklaniyor.
    public class JsonParse extends AsyncTask<String,Void,Void>
    {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowChoosenFilm.this);
            progressDialog.setMessage("loading");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... param) {
            try {
                //Gerekli url adresi aliniyor ve bu adrese baglanma islemi gerceklesiyor.
                URL url = new URL(param[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();

                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line+"\n");
                }
                String finalJson = buffer.toString();
                JSONObject jsonObject = new JSONObject(finalJson);

                //Elde edilen verilerin parse islemi gerceklesiyor.
                title = jsonObject.optString("Title");
                released = jsonObject.optString("Released");
                runtime = jsonObject.optString("Runtime");
                genre = jsonObject.optString("Genre");
                director = jsonObject.optString("Director");
                actor = jsonObject.optString("Actors");
                plot = jsonObject.optString("Plot");
                awards = jsonObject.optString("Awards");
                poster = jsonObject.optString("Poster");
                imdbRating = jsonObject.optDouble("imdbRating");
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null)
                {
                    connection.disconnect();
                }
                try {
                    if(bufferedReader != null)
                    {
                        bufferedReader.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog != null)
            {
                progressDialog.dismiss();
            }
        }
    }

    /*Ekranda bulunan yerlere elde edilen veriler yerlestiriliyor. Eger film ile ilgili veriler eksik ise film bilgisi olmadigi
        icin ilgili yerlere Unknown image,Unknown gibi yazilar ve resimler yerlestirilmisitr.*/
    public void createScreen() {
        posterC = (ImageView) findViewById(R.id.posterC);
        titleC = (TextView) findViewById(R.id.titleC);
        releasedC = (TextView) findViewById(R.id.releasedC);
        runtimeC = (TextView) findViewById(R.id.runtimeC);
        genreC = (TextView) findViewById(R.id.genreC);
        plotC = (TextView) findViewById(R.id.plotC);
        directorC = (TextView) findViewById(R.id.directorC);
        actorC = (TextView) findViewById(R.id.actorC);
        awardsC = (TextView) findViewById(R.id.awardsC);
        ratingBarC = (RatingBar) findViewById(R.id.ratingBarC);
        imdbRatingC = (TextView)findViewById(R.id.imdbRatingC);

        if (poster.equals("N/A")) {
            posterC.setImageDrawable(getResources().getDrawable(R.mipmap.not_found));
        } else {
            Picasso.with(this).load(poster).resize(155, 225).into(posterC);
        }
        titleC.setText(title);
        titleC.setGravity(Gravity.CENTER);
        if (released.equals("N/A")) {
            releasedC.setText("Released : Unknown");
        }
        else {
            releasedC.setText("Released : " + released);
        }
        if (runtime.equals("N/A")) {
            runtimeC.setText("Runtime : Unknown");
        } else {
            runtimeC.setText("Runtime : " + runtime);
        }
        if (genre.equals("N/A")) {
            genreC.setText("Genre : Unknown");
        } else {
            genreC.setText("Genre : " + genre);
        }
        String rating = ""+imdbRating;
        if(rating.equals("NaN"))
        {
            imdbRatingC.setText("IMDB Rating : -");
            ratingBarC.setVisibility(View.INVISIBLE);
        }
        else {
            imdbRatingC.setText("IMDB Rating : " + imdbRating);
        }
        if (plot.equals("N/A")) {
            plotC.setVisibility(View.INVISIBLE);
        } else {
            plotC.setText("     " + plot);
        }
        if (director.equals("N/A")) {
            directorC.setText("Director : Unknown");
        } else {
            directorC.setText("Director : " + director);
        }
        if (actor.equals("N/A")) {
            actorC.setText("Actors : Unknown");
        } else {
            actorC.setText("Actors : " + actor);
        }
        if (awards.equals("N/A")) {
            awardsC.setText("Awards : -");
        } else {
            awardsC.setText("Awards : " + awards);
        }
        ratingBarC.setRating((float) imdbRating / 10 * 5);
    }
}