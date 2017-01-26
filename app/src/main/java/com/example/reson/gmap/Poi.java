package com.example.reson.gmap;

/**
 * Created by 03070048 on 2016/12/7.
 */

//物件類別class，名稱為Poi
public class Poi
{
    private String Name;        //景點店家名稱
    private double Latitude;    //景點店家緯度
    private double Longitude;   //景點店家經度
    private double Distance;    //景點店家距離

    //建立物件時需帶入景點店家名稱、景點店家緯度、景點店家經度
    public Poi(String name , double latitude , double longitude)
    {
        //將資訊帶入類別屬性
        Name = name ;
        Latitude = latitude ;
        Longitude = longitude ;
    }

    //取得店家名稱
    public String getName()
    {
        return Name;
    }

    //取得店家緯度
    public double getLatitude()
    {
        return Latitude;
    }

    //取得店家經度
    public double getLongitude()
    {
        return Longitude;
    }

    //寫入店家距離
    public void setDistance(double distance)
    {
        Distance = distance;
    }

    //取的店家距離
    public double getDistance()
    {
        return Distance;
    }
}