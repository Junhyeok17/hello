package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class Translator{
    public static String translation(String word){
        final String clientID = "dP8A68rUVMrBppFbLrLn";
        final String secret = "4VoI6Oq3Be";

        try{
            String text = URLEncoder.encode(word, "UTF-8");
            String wordSource = URLEncoder.encode("en", "UTF-8");
            String wordTarget = URLEncoder.encode("ko", "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientID);
            con.setRequestProperty("X-Naver-Client-Secret", secret);

            String postParams = "source="+wordSource+"&target="+wordTarget+"&text="+text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode==200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine=br.readLine())!=null){
                response.append(inputLine);
            }
            br.close();

            String s = response.toString();
            s = s.split("\"")[27];
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}

class WeatherData{
    private String nx = "55"; // 경도
    private String ny = "127"; // 위도
    private String baseDate = "20220212";
    private String baseTime = "0500";
    private String type = "json";
    private String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private String serviceKey = "XcAh%2BRByEpU9NlzYGfX3FttHzKBy%2FVYDvFln2P7Cbw5uI46bEXd9F3SkbpDiFVBAd9n2dTwrd7qc2FaCCCx8YQ%3D%3D";
    public void lookUpWeather() throws IOException, JSONException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder urlBuilder = new StringBuilder(apiUrl);
                    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
                    urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
                    urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
                    urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
                    urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
                    urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));

                    URL url = new URL(urlBuilder.toString());
                    Log.d("url", url.toString());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-type", "application/json");

                    BufferedReader br;
                    if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300)
                        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    else
                        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    br.close();
                    connection.disconnect();
                    String result = stringBuilder.toString();

                    Log.d("result", result);
                    JSONObject object1 = new JSONObject(result);
                    Log.d("response", object1.toString());
                    JSONObject response = (JSONObject) object1.get("response");

                    //JSONObject object2 = new JSONObject(response);
                    Log.d("body", response.toString());
                    JSONObject body = (JSONObject) response.get("body");

                    //JSONObject object3 = new JSONObject(body);
                    Log.d("item", body.toString());
                    JSONObject items = (JSONObject) body.get("items");

                    //JSONObject object4 = new JSONObject(items);
                    JSONArray jsonArray = (JSONArray) items.get("item");

                    String weather = null, temperature = null;
                    JSONObject object4;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object4 = jsonArray.getJSONObject(i);
                        String fcstValue = object4.getString("fcstValue");
                        String category = object4.getString("category");

                        if (category.equals("SKY")) {
                            weather = "현재 날씨는 ";

                            if (fcstValue.equals("1"))
                                weather += "맑은 상태";
                            else if (fcstValue.equals("2"))
                                weather += "비가 오는 상태";
                            else if (fcstValue.equals("3"))
                                weather += "구름이 많은 상태";
                            else if (fcstValue.equals("4"))
                                weather += "흐린 상태";
                        }

                        temperature = "기온은 " + fcstValue + "도입니다.";

                        if(weather!=null)
                            Log.d("weather tag", weather + temperature);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

public class WeatherActivity extends  AppCompatActivity{

    private Geocoder geocoder;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_activity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherData weatherData = new WeatherData();
                try {
                    geocoder = new Geocoder(WeatherActivity.this);
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT>=23 && ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest
                                .permission.ACCESS_FINE_LOCATION}, 0);
                        Log.d("실패", "씰패");
                    }
                    else {
                        Criteria criteria = new Criteria();
                        String bestProvider = String.valueOf(lm.getBestProvider(criteria, true)).toString();
                        Location location = lm.getLastKnownLocation(bestProvider);

                        if (location != null) {
                            String provider = location.getProvider();
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            double altitude = location.getAltitude();

                            Log.d("long", longitude + ""); // 경도
                            Log.d("lat", latitude + ""); // 위도
                            Log.d("alt", altitude + ""); // 고도
                            try {
                                List<Address> address = null;
                                address = geocoder.getFromLocation(latitude, longitude, 5);
                                if (address.size() == 0)
                                    Log.d("null", "null");
                                else {
                                    Log.d("address", address.get(0).getAddressLine(0));
                                    String[] add = address.get(0).getAddressLine(0).split(", ");
                                    Log.d("add", add[1]);

                                    Log.d("구 이름", Translator.translation(add[1]));

                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm aa");
                                    String getTime = dateFormat.format(date);
                                    String[] times = getTime.split(" ");
                                    getTime = times[0]+(times[2].equals("PM") ? " 오후 " : " 오전 ")+times[1];
                                    String fin = getTime;
                                    Log.d("시간", fin);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    weatherData.lookUpWeather();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

/*
public class WeatherActivity extends AppCompatActivity {

    private TextView curlocation, curdate, curTemp, curHumid, curPrecipitation;
    private TextView day1, day2, day3, day4, day5, day6;
    private TextView utemp1, dtemp1, utemp2, dtemp2, utemp3, dtemp3;
    private TextView utemp4, dtemp4, utemp5, dtemp5, utemp6, dtemp6;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_activity);

        curlocation = (TextView)findViewById(R.id.location);
        curdate = (TextView)findViewById(R.id.curdate);
        curTemp = (TextView)findViewById(R.id.curtemp);
        curHumid = (TextView)findViewById(R.id.humidity);
        curPrecipitation = (TextView)findViewById(R.id.precipitation);

        day1 = (TextView)findViewById(R.id.day1);
        day2 = (TextView)findViewById(R.id.day2);
        day3 = (TextView)findViewById(R.id.day3);
        day4 = (TextView)findViewById(R.id.day4);
        day5 = (TextView)findViewById(R.id.day5);
        day6 = (TextView)findViewById(R.id.day6);

        utemp1 = (TextView)findViewById(R.id.utemper1);
        dtemp1 = (TextView)findViewById(R.id.dtemper1);
        utemp2 = (TextView)findViewById(R.id.utemper2);
        dtemp2 = (TextView)findViewById(R.id.dtemper2);
        utemp3 = (TextView)findViewById(R.id.utemper3);
        dtemp3 = (TextView)findViewById(R.id.dtemper3);
        utemp4 = (TextView)findViewById(R.id.utemper4);
        dtemp4 = (TextView)findViewById(R.id.dtemper4);
        utemp5 = (TextView)findViewById(R.id.utemper5);
        dtemp5 = (TextView)findViewById(R.id.dtemper5);
        utemp6 = (TextView)findViewById(R.id.utemper6);
        dtemp6 = (TextView)findViewById(R.id.dtemper6);

        geocoder = new Geocoder(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT>=23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest
                    .permission.ACCESS_FINE_LOCATION}, 0);
        }
        else{
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(lm.getBestProvider(criteria, true)).toString();
            Location location = lm.getLastKnownLocation(bestProvider);

            if(location!=null) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();

                Log.d("long", longitude+"");
                Log.d("lat", latitude+"");
                Log.d("alt", altitude+"");
                try {
                    List<Address> address = null;
                    address = geocoder.getFromLocation(latitude, longitude, 5);
                    if(address.size()==0)
                        Log.d("null", "null");
                    else {
                        Log.d("address", address.get(0).getAddressLine(0));
                        String[] add = address.get(0).getAddressLine(0).split(", ");
                        Log.d("add", add[1]);
                        String[] addr = address.get(0).getAddressLine(0).split(" ");
                        String lastAddr = addr[2]+", "+addr[3];
                        String url = "https://www.google.com/search?q="+add[1]+"+날씨&newwindow=1&sxsrf=APq-WBuU5Nb7qm-M6GcwwZXUhQShmuEO8Q%3A1644389151382&ei=H2MDYsXZFtDpwQOU7LL4DA&ved=0ahUKEwiF5fX5gvL1AhXQdHAKHRS2DM8Q4dUDCA4&uact=5&oq="+add[1]+"+날씨&gs_lcp=Cgdnd3Mtd2l6EAMyCggAEIAEEEYQgAIyBggAEAgQHjIGCAAQCBAeMgYIABAIEB4yBggAEAgQHjIGCAAQCBAeMgYIABAFEB4yBggAEAUQHjIGCAAQBRAeMgYIABAFEB46EQguEIAEELEDEIMBEMcBEK8BOgsIABCABBCxAxCDAToICC4QgAQQsQM6CAgAEIAEELEDOgsILhCABBCxAxCDAToFCC4QgAQ6BAgAEEM6BwguELEDEEM6CwguEIAEEMcBEK8BOgUIABCABDoPCAAQgAQQhwIQFBBGEIACOgkIABBDEEYQgAI6CggAEIAEEIcCEBQ6BAgAEB5KBAhBGABKBAhGGABQAFi7CGCbCmgAcAF4AIABkQGIAf4OkgEEMC4xNJgBAKABAcABAQ&sclient=gws-wiz";
                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    Document doc = (Document) Jsoup.connect(url).get();
                                    Elements elements = doc.select("div.UQt4rd");
                                    Log.d("cont", doc.text());
                                    elements = elements.select("span");
                                    Log.d("cont", elements.text());
                                    String currentTemperature = elements.first().text();
                                    Log.d("온도", currentTemperature+"");

                                    int[] temperatures = new int[8];
                                    elements = doc.select(".wtsRwe");
                                    elements = elements.select("div");
                                    String precipitation = elements.get(1).text();
                                    String humidity = elements.get(2).text();
                                    Log.d("강수", precipitation);
                                    Log.d("습도", humidity);

                                    elements = doc.select("div.wob_dfc");
                                    elements = elements.select("div");
                                    String[] ele = elements.text().toString().substring(0, 80).split(" ");
                                    Log.d("size", ele.length+"");
                                    String[] days = new String[20];
                                    String[][] tem = new String[20][2];
                                    int daysidx = 0, temp = 0;
                                    for(int i=0;i<ele.length;i++){
                                        if(i%3==0)
                                            days[daysidx++] = ele[i];
                                        else if(i%3==1){
                                            tem[temp][0] = ele[i].substring(0, ele[i].length()-3);
                                        } else if(i%3==2){
                                            tem[temp++][1] = ele[i].substring(0, ele[i].length()-3);
                                        }
                                        if(i==18)
                                            break;
                                    }

                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm aa");
                                    String getTime = dateFormat.format(date);
                                    String[] times = getTime.split(" ");
                                    getTime = times[0]+(times[2].equals("PM") ? " 오후 " : " 오전 ")+times[1];
                                    String fin = getTime;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            curdate.setText(fin);
                                            //curTemp.setText(currentTemperature+"°C");
                                            curHumid.setText(humidity);
                                            curPrecipitation.setText(precipitation);

                                            day1.setText(days[0]);
                                            day2.setText(days[1]);
                                            day3.setText(days[2]);
                                            day4.setText(days[3]);
                                            day5.setText(days[4]);
                                            day6.setText(days[5]);

                                            utemp1.setText(tem[0][0]);
                                            dtemp1.setText(tem[0][1]);
                                            utemp2.setText(tem[1][0]);
                                            dtemp2.setText(tem[1][1]);
                                            utemp3.setText(tem[2][0]);
                                            dtemp3.setText(tem[2][1]);
                                            utemp4.setText(tem[3][0]);
                                            dtemp4.setText(tem[3][1]);
                                            utemp5.setText(tem[4][0]);
                                            dtemp5.setText(tem[4][1]);
                                            utemp6.setText(tem[5][0]);
                                            dtemp6.setText(tem[5][1]);
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Thread thread2 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String resultWord = Translator.translation(lastAddr);
                                Log.d("ressult", resultWord);
                                curlocation.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        curlocation.setText(resultWord.substring(0, resultWord.length()-1));
                                    }
                                });
                            }
                        });
                        thread.start();
                        thread2.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                lm.requestLocationUpdates(bestProvider, 1000, 0, gpsLocationListener);
            }

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
        }
    }
    LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }
    };
}
 */