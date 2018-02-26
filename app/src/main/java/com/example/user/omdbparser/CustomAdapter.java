package com.example.user.omdbparser;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//Bu sinif olusturulmus olan listViewin gosterilmesi icin kullanilacaktir.
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList filmList;
    private TextView name , year;
    private ImageView poster;

    //Bu sinifa ait olan constructor.
    public CustomAdapter(Activity activity, LinkedHashMap<String, InformationAboutFilm> films) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = activity;
        filmList = new ArrayList();
        filmList.addAll(films.entrySet());
    }

    /*Bu fonksiyon toplamda kac adet film oldugunu dondurur.
    @return filmList.size() : list'de yer alan film sayisi*/
    @Override
    public int getCount() {
        return filmList.size();
    }

    /*Verilen position degerine gore o listede yer alan veri geri dondurulur
    @param position : Isleme sokulacak olan degerin sirasi
    @return filmList.get(position) : İstenen film bilgileri elde ediliyor.*/
    @Override
    public Map.Entry<String,InformationAboutFilm> getItem(int position) {
        return (Map.Entry)filmList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //Sirasi gelen ve gosterilecek olan film bilgileri bu bolumde ilgili textView'a ve ImageView a yerlestirme islemi gerceklestiriliyor.
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View satirView;

        if(view == null)
        {
            satirView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_satir,viewGroup,false);
        }
        else
        {
            satirView = view;
        }
        //İlklendirme islemi gerceklestiriliyor.
        name = (TextView) satirView.findViewById(R.id.name);
        year = (TextView) satirView.findViewById(R.id.year);
        poster = (ImageView) satirView.findViewById(R.id.poster);
        //İlgili deger position ile birlikte aliniyor.
        Map.Entry<String, InformationAboutFilm> item = getItem(position);
        //Yerlestirme islemi yapiliyor.
        name.setText(item.getValue().getTitle());
        name.setBackground(context.getResources().getDrawable(R.drawable.line));
        year.setText(item.getValue().getYear() + "");
        year.setBackground(context.getResources().getDrawable(R.drawable.line));
        if(item.getValue().getPoster().equals("N/A")) {
            poster.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_found));
            poster.setBackground(context.getResources().getDrawable(R.color.Black));
        }
        else {
            Picasso.with(context).load(item.getValue().getPoster()).resize(165, 175).into(poster);
        }
        return satirView;
    }
}