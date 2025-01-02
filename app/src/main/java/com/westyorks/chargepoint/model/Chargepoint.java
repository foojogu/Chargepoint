package com.westyorks.chargepoint.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chargepoints")
public class Chargepoint implements Parcelable {
    @PrimaryKey
    @NonNull
    private String referenceId = "";  // Default empty string to ensure non-null
    private String name;
    private String town;
    private String county;
    private String postcode;
    private String chargerStatus;
    private String connectorId;
    private String chargerType;
    private double latitude;
    private double longitude;

    public Chargepoint() {
    }

    protected Chargepoint(Parcel in) {
        referenceId = in.readString();
        name = in.readString();
        town = in.readString();
        county = in.readString();
        postcode = in.readString();
        chargerStatus = in.readString();
        connectorId = in.readString();
        chargerType = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @NonNull
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(@NonNull String referenceId) { this.referenceId = referenceId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }
    
    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }
    
    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    
    public String getChargerStatus() { return chargerStatus; }
    public void setChargerStatus(String chargerStatus) { this.chargerStatus = chargerStatus; }
    
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    
    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(referenceId);
        dest.writeString(name);
        dest.writeString(town);
        dest.writeString(county);
        dest.writeString(postcode);
        dest.writeString(chargerStatus);
        dest.writeString(connectorId);
        dest.writeString(chargerType);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chargepoint> CREATOR = new Creator<Chargepoint>() {
        @Override
        public Chargepoint createFromParcel(Parcel in) {
            return new Chargepoint(in);
        }

        @Override
        public Chargepoint[] newArray(int size) {
            return new Chargepoint[size];
        }
    };
}