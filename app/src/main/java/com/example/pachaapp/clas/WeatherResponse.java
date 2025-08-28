package com.example.pachaapp.clas;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("coord")
    private Coord coord;
    
    @SerializedName("weather")
    private List<Weather> weather;
    
    @SerializedName("base")
    private String base;
    
    @SerializedName("main")
    private Main main;
    
    @SerializedName("visibility")
    private int visibility;
    
    @SerializedName("wind")
    private Wind wind;
    
    @SerializedName("clouds")
    private Clouds clouds;
    
    @SerializedName("dt")
    private long dt;
    
    @SerializedName("sys")
    private Sys sys;
    
    @SerializedName("timezone")
    private int timezone;
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("cod")
    private int cod;

    public Coord getCoord() { return coord; }
    public List<Weather> getWeather() { return weather; }
    public String getBase() { return base; }
    public Main getMain() { return main; }
    public int getVisibility() { return visibility; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public long getDt() { return dt; }
    public Sys getSys() { return sys; }
    public int getTimezone() { return timezone; }
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCod() { return cod; }

    public static class Coord {
        @SerializedName("lon")
        private double lon;
        @SerializedName("lat")
        private double lat;
        
        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class Weather {
        @SerializedName("id")
        private int id;
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String icon;
        
        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        
        public String getIconUrl() {
            return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;
        @SerializedName("pressure")
        private int pressure;
        @SerializedName("humidity")
        private int humidity;
        @SerializedName("sea_level")
        private Integer seaLevel;
        @SerializedName("grnd_level")
        private Integer grndLevel;
        
        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
        public Integer getSeaLevel() { return seaLevel; }
        public Integer getGrndLevel() { return grndLevel; }
        
        public String getTempFormatted() {
            return Math.round(temp) + "°C";
        }
        
        public String getTempMinFormatted() {
            return Math.round(tempMin) + "°C";
        }
        
        public String getTempMaxFormatted() {
            return Math.round(tempMax) + "°C";
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;
        @SerializedName("deg")
        private int deg;
        @SerializedName("gust")
        private Double gust;
        
        public double getSpeed() { return speed; }
        public int getDeg() { return deg; }
        public Double getGust() { return gust; }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;
        
        public int getAll() { return all; }
    }

    public static class Sys {
        @SerializedName("country")
        private String country;
        @SerializedName("sunrise")
        private long sunrise;
        @SerializedName("sunset")
        private long sunset;
        
        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }
}
