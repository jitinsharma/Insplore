package io.github.jitinsharma.insplore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 16/07/16.
 */
public class InspireSearchObject implements Parcelable{
    String destinationCode;
    String depDate;
    String arrDate;
    String price;
    String airlineCode;
    String depCode;
    String destinationCity;
    String currencyCode;
    boolean placeEnabled;

    public InspireSearchObject() {

    }

    protected InspireSearchObject(Parcel in) {
        destinationCode = in.readString();
        depDate = in.readString();
        arrDate = in.readString();
        price = in.readString();
        airlineCode = in.readString();
        depCode = in.readString();
        destinationCity = in.readString();
        currencyCode = in.readString();
        placeEnabled = in.readByte() != 0;
    }

    public static final Creator<InspireSearchObject> CREATOR = new Creator<InspireSearchObject>() {
        @Override
        public InspireSearchObject createFromParcel(Parcel in) {
            return new InspireSearchObject(in);
        }

        @Override
        public InspireSearchObject[] newArray(int size) {
            return new InspireSearchObject[size];
        }
    };

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public String getDepDate() {
        return depDate;
    }

    public void setDepDate(String depDate) {
        this.depDate = depDate;
    }

    public String getArrDate() {
        return arrDate;
    }

    public void setArrDate(String arrDate) {
        this.arrDate = arrDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }


    public boolean isPlaceEnabled() {
        return placeEnabled;
    }

    public void setPlaceEnabled(boolean placeEnabled) {
        this.placeEnabled = placeEnabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(destinationCode);
        parcel.writeString(depDate);
        parcel.writeString(arrDate);
        parcel.writeString(price);
        parcel.writeString(airlineCode);
        parcel.writeString(depCode);
        parcel.writeString(destinationCity);
        parcel.writeString(currencyCode);
        parcel.writeByte((byte) (placeEnabled ? 1 : 0));
    }
}
