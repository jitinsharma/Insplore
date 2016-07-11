package io.github.jitinsharma.insplore.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by jitin on 01/07/16.
 */
public class TopDestinationDate implements Parcelable{
    Date minDate;
    Date maxDate;
    String data;

    public TopDestinationDate() {

    }

    protected TopDestinationDate(Parcel in) {
        data = in.readString();
    }

    public static final Creator<TopDestinationDate> CREATOR = new Creator<TopDestinationDate>() {
        @Override
        public TopDestinationDate createFromParcel(Parcel in) {
            return new TopDestinationDate(in);
        }

        @Override
        public TopDestinationDate[] newArray(int size) {
            return new TopDestinationDate[size];
        }
    };

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
    }
}
