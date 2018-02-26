package com.example.user.omdbparser;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String urlName = "http://www.omdbapi.com/?apikey=852159f0&s=" , newUrlName , movieName = null , response = "False" , send ;
    public static final String EXTRA = "extra";
    private LinearLayout lLayout;
    private EditText enterMovie;
    private StringBuffer buffer;
    private Button show;
    private WifiReceiver wifiReceiver;
    private MobileReceiver mobileReceiver;
    private boolean wifi, mobile;
    private TextView information;
    private ImageButton microphone;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        showButtonHandler();

        //Wi-fi ile internet baglantisinin olup olmadiginin kontrolu islemi icin gereklidir.
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, filter);

        //Mobile internete bagli olunup olunmadigi kontrolu islemi icin gereklidir.
        mobileReceiver = new MobileReceiver();
        registerReceiver(mobileReceiver, filter);

        //Kisinin soyleyecegi film ismini yaziya cevirmek icin kullanilir.
        microphoneHandler();
    }

    //İlklendirme islemleri gerceklestiriliyor.
    public void initialize()
    {
        lLayout = (LinearLayout)findViewById(R.id.lLayout);
        lLayout.setBackground(getResources().getDrawable(R.drawable.background));
        enterMovie = (EditText) findViewById(R.id.enterMovie);
        show = (Button) findViewById(R.id.show);
        information = (TextView)findViewById(R.id.information);
        microphone = (ImageButton)findViewById(R.id.microphone);
    }

    //Microfon işlemleri icin listener tanimlamasi yapiliyor.
    public void microphoneHandler()
    {
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput();
            }
        });
    }

    //Kisinin soyleyecegi seyleri yakalayip bu sesi istenen yere yerlestirilmesini saglayan kod parcasidir.
    public void startVoiceInput()
    {
        //Gerekli ilişkilendirme islemleri gerceklestiriliyor.
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Say Something!");
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        //Microfonudan gelen sesi cevirme ozelligi bulunmamasi durumunda kullaniciya bir mesaj yayinlanma islemi
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(MainActivity.this,"Sorry your device does not support speech to text",Toast.LENGTH_SHORT).show();
            microphone.setVisibility(View.INVISIBLE);
        }
    }

    //Cevrilen ses istenen TextView nesnesine yerlestiriliyor.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    enterMovie.setText(result.get(0));
                }
                break;
            }
        }
    }

    //Wi-fi durumunun ne oldugu, wi-fi degisimlerinin kontrol edilmesini saglar.
    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiCheck = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //Wifi baglantisinin olup olmadigi kontrol ediliyor.
            if (wifiCheck.isConnected()) {
                wifi = true;
            } else {
                wifi = false;
            }
            changeVisibility();
        }
    }

    //Mobilde internete bagli olunup olunmadigini ve internet degisimlerinin yakalandigi kisim
    public class MobileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent ıntent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileCheck = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            //Mobilde internetin bagli olup olmadigi kontrol ediliyor.
            if (mobileCheck.isConnected()) {
                mobile = true;
            } else {
                mobile = false;
            }
            changeVisibility();
        }
    }

    /*Internet baglantisinin olmamasi durumunda kullaniciya uygulamayi kullanma imkani verilmiyor. Cunku ilgili veriler
    internet uzerinden erisilip kullaniciya gosteriliyor.*/
    public void changeVisibility() {
        //Internet baglantisinin olmasi durumunu ifade eder.
        if (mobile || wifi) {
            enterMovie.setVisibility(View.VISIBLE);
            show.setVisibility(View.VISIBLE);
            information.setText(getResources().getString(R.string.information));
            information.setTextColor(getResources().getColor(R.color.Black));
            microphone.setVisibility(View.VISIBLE);
        }
        //Internetin bagli olmamasi durumunu ifade eder.
        else {
            enterMovie.setVisibility(View.INVISIBLE);
            show.setVisibility(View.INVISIBLE);
            information.setText(getResources().getString(R.string.warn));
            information.setTextColor(getResources().getColor(R.color.Red));
            microphone.setVisibility(View.INVISIBLE);
        }
    }

    /*Imdb uygulamasini kullanan kullanici goster butonuna tikladigi zaman eger ilgili film ile ilgili bilgiler mevcut ise
    bu bilgiler ekrana getiriliyor*/
    public void showButtonHandler()
    {
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieName = enterMovie.getText().toString();
                if(movieName != null)
                {
                    movieName = movieName.toLowerCase();
                    newUrlName = urlName+""+movieName;
                    new JsonParse().execute(newUrlName);
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(send != null) {
                        Intent intent = new Intent(MainActivity.this, Films.class);
                        intent.putExtra(EXTRA, send);
                        startActivity(intent);
                    }
                    else
                    {
                        enterMovie.setText("");
                    }
                }
            }
        });
    }

    //Arka planda film bilgilerinin alinmasi icin kullanilan siniftir.
    public class JsonParse extends AsyncTask<String,Void,Void>
    {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader;
        ProgressDialog progressDialog;
        //Gerekli bilgiler alinana kadar yuklendigi bilgisi kullaniciya gosteriliyor.
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("loading");
            progressDialog.show();
        }

        //İstenen url adresine baglanti gerceklestiriliyor ve Json verilerin teker teker alinmasi islemi gerceklestiriliyor.
        @Override
        protected Void doInBackground(String... param) {
            try {
                //Verilen url bilgisi aliniyor ve baglanma islemi gerceklestiriliyor.
                URL url = new URL(param[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                //Ulasilan veriler bir String degere yerlestiriliyor.
                String line = null;
                buffer = new StringBuffer();
                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line+"\n");
                }
                String finalJson = buffer.toString();

                //Bu string nesnesi kullanilarak Json veriye donusturuluyor ve bu verinin var olup olmadigi kontrol ediliyor.
                JSONObject jsonObject = new JSONObject(finalJson);
                response = jsonObject.optString("Response");
                if(response.equals("True")) {
                    send = finalJson;
                }
                else
                {
                    send = null;
                }
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

        //Daha onceden kullaniciya gosterilen yukleniyor ibaresi kaldiriliyor.
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog != null)
            {
                progressDialog.dismiss();
            }
        }
    }
}