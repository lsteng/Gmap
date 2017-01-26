package com.example.reson.gmap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03070048 on 2016/12/12.
 */

public class LocationUpdate implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private static final String TAG = "LocationUpdate";
    private Context mContext;

    private GoogleApiClient googleApiClient;    // Google API用戶端物件
    private LocationRequest locationRequest;    // Location請求物件
    private Location currentLocation;           // 記錄目前最新的位置
//    private Marker currentMarker, itemMarker;   // 顯示目前與儲存位置的標記物件

//    private ArrayList<Poi> Pois = new ArrayList<Poi>(); //建立List，屬性為Poi物件
    private ArrayList<Poi> Pois; //建立List，屬性為Poi物件

    public LocationUpdate(Context context) {
        mContext = context;

        // 建立Google API用戶端物件
        configGoogleApiClient();

        // 建立Location請求物件
        configLocationRequest();

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        //建立物件，並放入List裡 (建立物件需帶入名稱、緯度、經度)
//        Pois.add(new Poi("三立電視" , 25.061510, 121.578613 ));
//        Pois.add(new Poi("台北車站" , 25.04661 , 121.5168 ));
//        Pois.add(new Poi("台中車站" , 24.13682 , 120.6850 ));
//        Pois.add(new Poi("台北101" , 25.03362 , 121.56500 ));
//        Pois.add(new Poi("高雄85大樓" , 22.61177 , 120.30031 ));
//        Pois.add(new Poi("九份老街" , 25.10988 , 121.84519 ));
        Pois = MainActivity.Pois;
    }

    // 建立Google API用戶端物件
    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services
        // 啟動位置更新服務
        // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged
        Log.d(TAG, "startPeriodicUpdates");
        int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "permission.ACCESS_FINE_LOCATION:"+permission);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "permissions Denied !");
            return; // return or app will FC
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    // ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
    }

    // OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(mContext, R.string.google_play_service_missing, Toast.LENGTH_LONG).show();
        }
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
        currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //測量距離
        calcDistance(latLng);
    }

    public void stopService(){
        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    //計算目前位置與清單位置的距離
    private void calcDistance(LatLng place){
        for(Poi mPoi : Pois)
        {
            //for迴圈將距離帶入，判斷距離為Distance function
            //需帶入使用者取得定位後的緯度、經度、景點店家緯度、經度。
            mPoi.setDistance(Distance(place.latitude,
                    place.longitude,
                    mPoi.getLatitude(),
                    mPoi.getLongitude()));
        }

        //依照距離遠近進行List重新排列
        DistanceSort(Pois);

        //印出我的座標-經度緯度
//        String X = String.valueOf(place.latitude).substring(0, 7);
//        String Y = String.valueOf(place.longitude).substring(0, 7);
//        mTV.setText("目前位置-"+"緯度: "+X + ", 經度: "+Y);

        //for迴圈，印出景點店家名稱及距離，並依照距離由近至遠排列
        //第一筆為最近的景點店家，最後一筆為最遠的景點店家
        for(int i = 0 ; i < Pois.size() ; i++ )
        {
            String title = "地點: "+Pois.get(i).getName();
            String msg   = "距離為: "+DistanceText(Pois.get(i).getDistance());

            int distanceNotify = MainActivity.distanceNotify;
            if(Pois.get(i).getDistance() < distanceNotify){
                notifyMSG(title, msg);
            }
        }

        //解析座標地址
//        mADD.setText(getAddressByLocation(null, place));
    }

    private void notifyMSG(String title, String text){
        final int notifyID = 1; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知
        final int requestCode = notifyID; // PendingIntent的Request Code
//        final Intent intent = ((Activity)mContext).getIntent(); // 目前Activity的Intent
        final Intent intent = new Intent(mContext, MainActivity.class);
//        final int flags = PendingIntent.FLAG_ONE_SHOT;       // ONE_SHOT：PendingIntent只使用一次；
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；
//        final int flags = PendingIntent.FLAG_NO_CREATE;      // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；
//        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel)
//                .build(); // 建立通知
                .getNotification();

        //notification.defaults |= Notification.DEFAULT_ALL;
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(notifyID, notification); // 發送通知
    }

    //帶入距離回傳字串 (距離小於一公里以公尺呈現，距離大於一公里以公里呈現並取小數點兩位)
    private String DistanceText(double distance)
    {
        if(distance < 1000) return String.valueOf((int)distance) + "m" ;
        else return new DecimalFormat("#.00").format(distance/1000) + "km" ;
    }

    //List排序，依照距離由近開始排列，第一筆為最近，最後一筆為最遠
    private void DistanceSort(ArrayList<Poi> poi)
    {
        Collections.sort(poi, new Comparator<Poi>()
        {
            @Override
            public int compare(Poi poi1, Poi poi2)
            {
                return poi1.getDistance() < poi2.getDistance() ? -1 : 1 ;
            }
        });
    }

    //帶入使用者及景點店家經緯度可計算出距離
    public double Distance(double longitude1, double latitude1, double longitude2,double latitude2)
    {
        double radLatitude1 = latitude1 * Math.PI / 180;
        double radLatitude2 = latitude2 * Math.PI / 180;
        double l = radLatitude1 - radLatitude2;
        double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(radLatitude1) * Math.cos(radLatitude2)
                * Math.pow(Math.sin(p / 2), 2)));
        distance = distance * 6378137.0;
        distance = Math.round(distance * 10000) / 10000;

        return distance ;
    }

    //座標反查地址
    public String getAddressByLocation(Location location, LatLng place) {
        String returnAddress = "";
        try {
            if (location != null || place != null) {
//                Double longitude = location.getLongitude();	//取得經度
//                Double latitude = location.getLatitude();	//取得緯度
                Double longitude = place.longitude;	//取得經度
                Double latitude = place.latitude;	//取得緯度

                //建立Geocoder物件
                Geocoder gc = new Geocoder(mContext, Locale.TRADITIONAL_CHINESE); 	//地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);
                returnAddress = lstAddress.get(0).getAddressLine(0);

//                lstAddress.get(0).getCountryName();  //台灣省
//                lstAddress.get(0).getAdminArea();  //台北市
//                lstAddress.get(0).getLocality();  //中正區
//                lstAddress.get(0).getThoroughfare();  //信陽街(包含路巷弄)
//                lstAddress.get(0).getFeatureName();  //會得到33(號)
//                lstAddress.get(0).getPostalCode();  //會得到100(郵遞區號)
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.d("getAddressByLocation", e.toString());
        }
        return returnAddress;
    }

    //地址轉座標
    public static LatLng getLocationByAddress(Context context, String addressString) {
//        Geocoder geocoder = new Geocoder(mContext, new Locale("zh", "TW"));
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressLocation = null;
        try{
            addressLocation = geoCoder.getFromLocationName(addressString, 1);
        }catch (IOException e){
            Log.d(TAG, e.toString());
        }
        double latitude = addressLocation.get(0).getLatitude();
        double longitude = addressLocation.get(0).getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }
}
