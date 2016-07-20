package io.github.jitinsharma.insplore.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 04/07/16.
 */
public class PoiObject implements Parcelable{
    String title;
    String mainImageUrl;
    String wikipediaLink;
    String poiLatitude;
    String poiLongitude;
    String poiDescription;
    String geoNameId;
    byte[] imageArray;

    public PoiObject() {
    }

    protected PoiObject(Parcel in) {
        title = in.readString();
        mainImageUrl = in.readString();
        wikipediaLink = in.readString();
        poiLatitude = in.readString();
        poiLongitude = in.readString();
        poiDescription = in.readString();
        geoNameId = in.readString();
        /*if (imageArray.length>0) {
            imageArray = new byte[in.readInt()];
        }
        in.readByteArray(imageArray);*/
    }

    public static final Creator<PoiObject> CREATOR = new Creator<PoiObject>() {
        @Override
        public PoiObject createFromParcel(Parcel in) {
            return new PoiObject(in);
        }

        @Override
        public PoiObject[] newArray(int size) {
            return new PoiObject[size];
        }
    };

    public String getPoiDescription() {
        return poiDescription;
    }

    public void setPoiDescription(String poiDescription) {
        this.poiDescription = poiDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getWikipediaLink() {
        return wikipediaLink;
    }

    public void setWikipediaLink(String wikipediaLink) {
        this.wikipediaLink = wikipediaLink;
    }

    public String getPoiLatitude() {
        return poiLatitude;
    }

    public void setPoiLatitude(String poiLatitude) {
        this.poiLatitude = poiLatitude;
    }

    public String getPoiLongitude() {
        return poiLongitude;
    }

    public void setPoiLongitude(String poiLongitude) {
        this.poiLongitude = poiLongitude;
    }

    public String getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(String geoNameId) {
        this.geoNameId = geoNameId;
    }

    public byte[] getImageArray() {
        return imageArray;
    }

    public void setImageArray(byte[] imageArray) {
        this.imageArray = imageArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(mainImageUrl);
        parcel.writeString(wikipediaLink);
        parcel.writeString(poiLatitude);
        parcel.writeString(poiLongitude);
        parcel.writeString(poiDescription);
        parcel.writeString(geoNameId);
        //parcel.writeByteArray(imageArray);
    }
}

