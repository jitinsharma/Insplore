package io.github.jitinsharma.insplore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 26/06/16.
 */
public class LocationObject implements Parcelable{
    double latitude;
    double longitude;
    String airportCode;
    String cityName;
    String date;
    int tabNumber;
    boolean tripSwitch;
    String tabSelection;

    public LocationObject() {
    }

    protected LocationObject(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        airportCode = in.readString();
        cityName = in.readString();
        date = in.readString();
        tabNumber = in.readInt();
        tripSwitch = in.readByte() != 0;
        tabSelection = in.readString();
    }

    public static final Creator<LocationObject> CREATOR = new Creator<LocationObject>() {
        @Override
        public LocationObject createFromParcel(Parcel in) {
            return new LocationObject(in);
        }

        @Override
        public LocationObject[] newArray(int size) {
            return new LocationObject[size];
        }
    };

    public String getTabSelection() {
        return tabSelection;
    }

    public void setTabSelection(String tabSelection) {
        this.tabSelection = tabSelection;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTabNumber() {
        return tabNumber;
    }

    public void setTabNumber(int tabNumber) {
        this.tabNumber = tabNumber;
    }

    public boolean isTripSwitch() {
        return tripSwitch;
    }

    public void setTripSwitch(boolean tripSwitch) {
        this.tripSwitch = tripSwitch;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(airportCode);
        parcel.writeString(cityName);
        parcel.writeString(date);
        parcel.writeInt(tabNumber);
        parcel.writeByte((byte) (tripSwitch ? 1 : 0));
        parcel.writeString(tabSelection);
    }
}
