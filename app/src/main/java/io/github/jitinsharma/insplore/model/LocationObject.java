package io.github.jitinsharma.insplore.model;

/**
 * Created by jitin on 26/06/16.
 */
public class LocationObject {
    double latitude;
    double longitude;
    String airportCode;
    String cityName;

    public String getTabSelection() {
        return tabSelection;
    }

    public void setTabSelection(String tabSelection) {
        this.tabSelection = tabSelection;
    }

    String tabSelection;

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
