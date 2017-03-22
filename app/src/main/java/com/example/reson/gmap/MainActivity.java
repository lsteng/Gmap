package com.example.reson.gmap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button openMap;
    private Button startButton, stopButton;
    private Button transferBtn;
    private EditText addressET, locationET;
    private RadioGroup radioGroup;
    private LinearLayout mainLL;

    public static int distanceNotify = 50;
    public static ArrayList<Poi> Pois = new ArrayList<Poi>(); //建立List，屬性為Poi物件

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openMap = (Button) findViewById(R.id.btn1);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        transferBtn = (Button) findViewById(R.id.transferBtn);
        addressET = (EditText) findViewById(R.id.addressET);
        locationET = (EditText) findViewById(R.id.locationET);
        radioGroup = (RadioGroup) findViewById(R.id.radioG);
        mainLL = (LinearLayout) findViewById(R.id.mainLL);

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        //建立物件，並放入List裡 (建立物件需帶入名稱、緯度、經度)
        if(Pois.size() < 1){
            Pois.add(new Poi("三立電視" , 25.061510, 121.578613 ));
        }
        settings = getSharedPreferences("Preference", MODE_PRIVATE);

        if(settings.getString("address", null) != null){
            String address = settings.getString("address", "");
            float latitude = settings.getFloat("latitude", 0);
            float longitude = settings.getFloat("longitude", 0);

            Pois.set(0, new Poi(address, latitude, longitude));
            addressET.setText(address);
            locationET.setText(latitude +","+ longitude);
        }else{
            addressET.setText("三立電視");
            locationET.setText("25.061510,121.578613");
        }

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressET.getText().toString().trim();

                LatLng latLng = LocationUpdate.getLocationByAddress(getApplicationContext(), address);
                locationET.setText(latLng.latitude+","+latLng.longitude);

                Pois.set(0, new Poi(address , latLng.latitude, latLng.longitude));

                settings.edit().putString("address", address).commit();
                settings.edit().putFloat("latitude", (float)(latLng.latitude)).commit();
                settings.edit().putFloat("longitude", (float)(latLng.longitude)).commit();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId){
                    case R.id.meter50:
                        distanceNotify =50;
                        break;
                    case R.id.meter100:
                        distanceNotify =100;
                        break;
                    case R.id.meter200:
                        distanceNotify =200;
                        break;
                    case R.id.meter300:
                        distanceNotify =300;
                        break;
                    case R.id.meter500:
                        distanceNotify =500;
                        break;
                }
            }
        });

        startButton.setOnClickListener(startClickListener);
        stopButton.setOnClickListener(stopClickListener);
    }

    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            //啟動服務
            Intent intent = new Intent(MainActivity.this, MyService.class);
            startService(intent);
//            Toast.makeText(getApplicationContext(), "Start Service", Toast.LENGTH_SHORT).show();
            Snackbar.make(mainLL, "Start Service", Snackbar.LENGTH_SHORT).show();
        }
    };

    private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            //停止服務
            Intent intent = new Intent(MainActivity.this, MyService.class);
            stopService(intent);
//            Toast.makeText(getApplicationContext(), "Stop Service", Toast.LENGTH_SHORT).show();
            Snackbar.make(mainLL, "Stop Service", Snackbar.LENGTH_SHORT).show();
        }
    };
}
