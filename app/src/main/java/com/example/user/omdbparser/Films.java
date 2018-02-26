package com.example.user.omdbparser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.example.user.omdbparser.MainActivity.EXTRA;

//Filmler hakkinda yuzeysel bilgilerin yer aldigi ve bu filmlere tiklama ile film bilgilerinin gosterilmesini saglayan siniftir.
public class Films extends Activity {

    public static final String IMDB_ID = "imdb_id";
    private LinkedHashMap<String, InformationAboutFilm> films;
    private ListView listView;
    private CustomAdapter customAdapter;
    private String imdbID = null;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        init();
        organizeScreen();
        clickHandler();
    }

    public void init() {
        films = new LinkedHashMap<String, InformationAboutFilm>();
    }

    public void organizeScreen() {
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra(EXTRA));
            JSONArray jsonArray = jsonObject.getJSONArray("Search");
            //Film ile ilgili veriler elde ediliyor ve ekranda gosterilmek amaciyla gerektigi sekliyle parcalama islemi yapiliyor.
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                //Bu kisimda verinin film olup olmadigi islemi gerceklestiriliyor.
                if (jObject.optString("Type").equals("movie")) {
                    films.put(jObject.optString("imdbID"),new InformationAboutFilm(jObject.optString("Title"),jObject.optInt("Year"),jObject.optString("Poster")));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        //Olusturulan listView nesnesine yine olusturulan customAdapter nesnesi set ediliyor.Bu islemle birlikte gerekli bilgilerin ekranda gosterme islemi yapiliyor
        listView = (ListView)findViewById(R.id.listView);
        customAdapter = new CustomAdapter(this,films);
        listView.setAdapter(customAdapter);
    }

    /*Bu metod kullaniciya gosterilen filmlere tiklama durumunda film ile ilgili verilerin alinmasi islemini gerceklestirir.Film
    bilgileri alinarak kullaniciya daha ayrintili bilgilerin gosterilmesi amaclanmistir.*/
    public void clickHandler()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = 0;
                boolean isFound = false;
                Set<String> set = films.keySet();
                Iterator<String> iterator = set.iterator();
                while ((!isFound)&&(iterator.hasNext()))
                {
                    String setElement = iterator.next();
                    //Tiklama sirasi bulunarak gerekli bilgiler elde ediliyor.
                    if(i == position)
                    {
                        imdbID = setElement;
                        isFound = true;
                    }
                    i++;
                }
                checkActivityControl();
            }
        });
    }

    public void checkActivityControl()
    {
        if(imdbID != null)
        {
            intent = new Intent(Films.this,ShowChoosenFilm.class);
            intent.putExtra(IMDB_ID,imdbID);
            startActivity(intent);
        }
    }
}