package in.co.eko.fundu.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HistoryUser implements Serializable{
    @SerializedName("id")
    private String id;
    @SerializedName("location")
    private double[] location;
    @SerializedName("rating")
    private double rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
