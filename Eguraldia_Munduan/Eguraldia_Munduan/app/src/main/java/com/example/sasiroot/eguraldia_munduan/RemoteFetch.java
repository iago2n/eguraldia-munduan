package com.example.sasiroot.eguraldia_munduan;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {

    private static final String API ="https://api.darksky.net/forecast/554fc93724742248c61a8c8c1f4ad946";


    public static JSONObject getJSON(Context context, String coord){
        try {
            coord = "42.3482,-75.1890";//debug
            URL url = new URL(String.format((API), coord));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            return null;
        }
    }
}