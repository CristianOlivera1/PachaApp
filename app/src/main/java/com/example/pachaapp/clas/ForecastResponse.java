package com.example.pachaapp.clas;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<ForecastItem> list;

    public List<ForecastItem> getList() { return list; }

    public static class ForecastItem {
        @SerializedName("dt_txt")
        private String dateTime;
        @SerializedName("main")
        private Main main;
        @SerializedName("weather")
        private List<Weather> weather;
        @SerializedName("wind")
        private Wind wind;

        public String getDateTime() { return dateTime; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
        public Wind getWind() { return wind; }
    }

    public static class Main {
        @SerializedName("temp")
        private float temp;
        @SerializedName("temp_min")
        private float tempMin;
        @SerializedName("temp_max")
        private float tempMax;

        public float getTemp() { return temp; }
        public float getTempMin() { return tempMin; }
        public float getTempMax() { return tempMax; }
    }

    public static class Weather {
        @SerializedName("icon")
        private String icon;

        public String getIcon() { return icon; }
    }

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        public float getSpeed() { return speed; }
    }

}
