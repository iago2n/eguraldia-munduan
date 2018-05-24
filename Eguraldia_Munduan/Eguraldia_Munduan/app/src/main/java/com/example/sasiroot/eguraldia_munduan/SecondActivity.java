package com.example.sasiroot.eguraldia_munduan;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by sasiroot on 3/9/18.
 */

public class SecondActivity extends Activity {

    private static final String API ="https://api.darksky.net/forecast/554fc93724742248c61a8c8c1f4ad946/%s"+"?units=si&lang=es";

    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    GifImageView gifView;
    TextView currentTemperatureField;
    int backgroundImageId = 0;

    Button new_york;
    Button paris;
    Button london;
    Button los_angeles;
    Button tokyo;

    String coord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        Bundle extras = getIntent().getExtras();

        System.out.print("LALALALA" + extras.get("CityName"));
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("CityName");
        setupNavBtn();

//        View lay = (View) findViewById(R.id.rLayout);
//        lay.setBackgroundResource(R.drawable.bluepink);

//        RelativeLayout baseLayout = (RelativeLayout) this.findViewById(R.id.the_layout_id);
//
//        Drawable drawable = loadImageFromAsset();
//
//        if(drawable != null){
//            baseLayout.setBackground(drawable);
//            Log.d("TheActivity", "Setting the background");
//        }

        chooseCity(cityName);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rLayout);
        rl.setBackgroundResource(backgroundImageId);

        int alphaAmount = 70;
        rl.getBackground().setAlpha(alphaAmount);

        //spacing issue

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //declare at the top if possible
        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
       // weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        cityField.setText(cityName);


        //Context myContext = this;
        chooseCity(cityName);
        final JSONObject json = getJSON(coord);
        //Log.v("JSON:", json.toString());
        renderWeather(json);



        eguraldiaDbOpenHelper eguraldiaDbOpenHelper= new eguraldiaDbOpenHelper(getApplicationContext(), "Eguraldia",null,1);
        SQLiteDatabase eguraldiaDB =
                eguraldiaDbOpenHelper.getReadableDatabase();

        // Egiaztatu datu-basea ondo ireki den
        if (eguraldiaDB == null) {
            // Sortu eta erauktsi jakinarazpen bat akats bat gertatu dela           erakusteko
            Toast.makeText(SecondActivity.this, "baaaaaaad", Toast.LENGTH_LONG).show();
            // Bukatu "onClick" metodoa

            return;

        }
        ContentValues erregistroa = new ContentValues();
        erregistroa.put("hiria",
                cityField.getText().toString());
        erregistroa.put("uneko_tenperatura",
                currentTemperatureField.getText().toString());
        erregistroa.put("komentarioa",
                updatedField.getText().toString());
        erregistroa.put("predikzioa",
                detailsField.getText().toString());

        // Exekutatu datu-basean datuak txertatzeko agindua
        eguraldiaDB.insert("Eguraldia", null, erregistroa);
        // Itxi datu-basea
        eguraldiaDbOpenHelper.close();
        // Sortu eta erakutsi jakinarazpen bat kontaktua ondo gehitu dela erakusteko
        //Toast.makeText(SecondActivity.this, "goooooood insert", Toast.LENGTH_LONG).show();


    }


    //instead of using case, build a database and retrieve from databasel.
    private void chooseCity(String cityName){
        switch (cityName){
            case("New York"):
                coord = "40.730610,-73.935242";
                backgroundImageId = R.drawable.nuevaayork;
                break;
            case("London"):
                coord = "51.5072,-0.1275";
                backgroundImageId = R.drawable.london;
                break;
            case("Los Angeles"):
                coord = "34.0500,-118.2500";
                backgroundImageId = R.drawable.losangeles;
                break;
            case("Paris"):
                coord = "48.8567,2.3508";
                backgroundImageId = R.drawable.paris;
                break;
            case("Tokyo"):
                backgroundImageId = R.drawable.tokyo;
                coord = "35.6833,139.6833";
                break;
            case("Arrankudiaga"):
                coord = "43.256963,-2.923441";
                backgroundImageId = R.drawable.arrankudiaga;
                break;
            case("Ziortza-Bolibar"):
                coord = "43.2497137,-2.5497100999999702";
                backgroundImageId = R.drawable.ziortza;
                break;
            case("Matxinbenta"):
                coord = "43.0778,-2.2278";
                backgroundImageId = R.drawable.matxibenta;
                break;
            case("Axpe"):
                coord = "43.1160000,-2.5985700";
                backgroundImageId = R.drawable.axpe;
                break;
            case("Baliarrain"):
                coord = "43.0729042,-2.1293729";
                backgroundImageId = R.drawable.baliarrain;
                break;
            default:
                coord = "42.3482,-75.1890";
                break;
        }
    }

    private void setupNavBtn(){
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "bigarren click", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    private void renderWeather(JSONObject json){
        try {
            //detailsField.setText("");
            //cityField.setText("");
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            Date currentTime = Calendar.getInstance().getTime();
            String[] days = {"DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"};
            JSONArray data_array = json.getJSONObject("daily").getJSONArray("data");
            for (int i=0; i<7;i++){
                JSONObject item = data_array.getJSONObject(i);


                String temperatureMax = item.getString("temperatureMax");
                String temperatureMin = item.getString("temperatureMin");

                String w_summary = item.getString("summary");
                temperatureMax = temperatureMax.substring(0,2);
                temperatureMin = temperatureMin.substring(0,2);


                detailsField.setText(detailsField.getText()  + days[(today+i)%7] + ": "+temperatureMin+" - "+temperatureMax +" "+w_summary+ "\n");
                detailsField.getText();
            }




            //cityField.setText("New York");
            if(json.getString("timezone").contains("York"))
                cityField.setText("New York");
            if(json.getString("timezone").contains("London"))
                cityField.setText("London");
            if(json.getString("timezone").contains("Los"))
                cityField.setText("Los Angeles");
            if(json.getString("timezone").contains("Paris"))
                cityField.setText("Paris");
            if(json.getString("timezone").contains("Tokyo"))
                cityField.setText("Tokyo");


            currentTemperatureField.setText(json.getJSONObject("currently").getString("temperature") + " \u00b0 C");
            updatedField.setText(

                    // "SUMMARY OF WEEK  : " +
                    json.getJSONObject("currently").getString("summary")
                    // +      "\nTIME ZONE  : " + json.getString("timezone")
            );
            String icon = json.getJSONObject("currently").getString("icon");
            GifDrawable r = null; //pl.droidsonroids.gif.GifDrawable.createFromPath();
            String [] bideak ={"res/drawable/rain","res/drawable/sunny","res/drawable.clear_night","res/drawable/snow","res/drawable/sleet",
                    "res/drawable/windy","res/drawable/foggy","res/drawable/cloudy","res/drawable/partly_cloudy","res/drawable/partly_cloudy_night"};//sleet, wind fog

            int [] iDs = new int[bideak.length];
            for (int i=0;i<bideak.length;i++) {
                int idenInt;
                idenInt = getResources().getIdentifier(backgroundImageId[i] , "GifDrawable", getPackageName();
                //iDs[i] = getResources().getIdentifier(IdRes[i] , "GifDrawable", getPackageName());

            }
                            //Drawable r=null;
            if (icon.compareToIgnoreCase("rain") == 0) {
               //Uri[] lekua = {from};

               r =new GifDrawable(getAssets(),"res/drawable/rain.gif");

                // r = getResources().getDrawable(R.drawable.rain);

                gifView.setBackgroundResource(iDs[0]);
                r.start();

            } else if (icon.compareToIgnoreCase("clear-day") == 0) {
                r=new GifDrawable("res/drawable/sunny.gif");
                 //  r = getResources().getDrawable(R.drawable.sunny);
                gifView.setBackgroundResource(iDs[1]);
                r.start();
               // weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("clear-night") == 0) {

                r=new GifDrawable("res/drawable/clear_night.gif");
                   //r = getResources().getDrawable(R.drawable.clear_night);
                gifView.setBackgroundResource(iDs[2]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("snow") == 0) {
                r=new GifDrawable("res/drawable/snow.gif");
                    //r = getResources().getDrawable(R.drawable.snow);
                gifView.setBackgroundResource(iDs[3]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("sleet") == 0) {
                r=new GifDrawable("res/drawable/snow.gif");
                   //r = getResources().getDrawable(R.drawable.snow);
                gifView.setBackgroundResource(iDs[4]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("wind") == 0) {
                r=new GifDrawable("res/drawable/windy.gif");
                  //r = getResources().getDrawable(R.drawable.windy);
                gifView.setBackgroundResource(iDs[5]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("fog") == 0) {
                r=new GifDrawable("res/drawable/fog.gif");
                   //r = getResources().getDrawable(R.drawable.foggy);
                gifView.setBackgroundResource(iDs[6]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("cloudy") == 0) {
                r=new GifDrawable("res/drawable/cloudy.gif");
                 // r = getResources().getDrawable(R.drawable.cloudy);
                gifView.setBackgroundResource(iDs[7]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("partly-cloudy-day") == 0) {
                r=new GifDrawable("res/drawable/partly_cloudy.gif");
                  //r = getResources().getDrawable(R.drawable.partly_cloudy);
                gifView.setBackgroundResource(iDs[8]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            } else if (icon.compareToIgnoreCase("partly-cloudy-night") == 0) {
                r=new GifDrawable("res/drawable/partly_cloudy_night.gif");
            //       r = getResources().getDrawable(R.drawable.partly_cloudy_night);
                gifView.setBackgroundResource(iDs[9]);
                r.start();
                //weatherIcon.setImageDrawable(r);
            }


        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }


    public static JSONObject getJSON(/*Context context,*/ String coord){
        try {
           // coord = "40.7127,-74.0059";//debug
            URL url = new URL(String.format((API), coord));

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.getInputStream();

            System.out.print("CONNECTION:::" + connection.getInputStream());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            System.out.print("url:::");
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

  /*  public Drawable setIrudi(JSONObject json) {

        JSONArray data_array = null;
        Drawable r = null;
        try {
            data_array = json.getJSONObject("daily").getJSONArray("data");
            for (int i = 0; i < 7; i++) {
                JSONObject item = data_array.getJSONObject(i);
                String icon = item.getString("icon");
                if (icon.compareToIgnoreCase("rain") == 0) {

                    r = getResources().getDrawable(R.drawable.jarreo);

                    weatherIcon.setImageDrawable(r);

                } else if (icon.compareToIgnoreCase("clear-day") == 0) {

                    r = getResources().getDrawable(R.drawable.sol);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("clear-night") == 0) {

                    r = getResources().getDrawable(R.drawable.luna);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("snow") == 0) {

                    r = getResources().getDrawable(R.drawable.nieve);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("sleet") == 0) {

                    r = getResources().getDrawable(R.drawable.nieve);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("wind") == 0) {

                    r = getResources().getDrawable(R.drawable.viento);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("fog") == 0) {

                    r = getResources().getDrawable(R.drawable.niebla);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("cloudy") == 0) {

                    r = getResources().getDrawable(R.drawable.nublado);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("partly-cloudy-day") == 0) {

                    r = getResources().getDrawable(R.drawable.nubes_claros);

                    weatherIcon.setImageDrawable(r);
                } else if (icon.compareToIgnoreCase("partly-cloudy-night") == 0) {

                    r = getResources().getDrawable(R.drawable.nubes_noche);

                    weatherIcon.setImageDrawable(r);
                }}


         catch (JSONException e) {
            e.printStackTrace();
        }

        return r;*/
    }
