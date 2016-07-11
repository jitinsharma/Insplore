package io.github.jitinsharma.insplore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 28/06/16.
 */
public class TopDestinationObject implements Parcelable{
    String destination;
    String noOfFlights;
    String noOfPax;
    String cityName;

    public TopDestinationObject() {
    }

    protected TopDestinationObject(Parcel in) {
        destination = in.readString();
        noOfFlights = in.readString();
        noOfPax = in.readString();
        cityName = in.readString();
    }

    public static final Creator<TopDestinationObject> CREATOR = new Creator<TopDestinationObject>() {
        @Override
        public TopDestinationObject createFromParcel(Parcel in) {
            return new TopDestinationObject(in);
        }

        @Override
        public TopDestinationObject[] newArray(int size) {
            return new TopDestinationObject[size];
        }
    };

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNoOfFlights() {
        return noOfFlights;
    }

    public void setNoOfFlights(String noOfFlights) {
        this.noOfFlights = noOfFlights;
    }

    public String getNoOfPax() {
        return noOfPax;
    }

    public void setNoOfPax(String noOfPax) {
        this.noOfPax = noOfPax;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(destination);
        parcel.writeString(noOfFlights);
        parcel.writeString(noOfPax);
        parcel.writeString(cityName);
    }
}
