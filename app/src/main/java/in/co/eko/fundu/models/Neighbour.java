package in.co.eko.fundu.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zartha on 4/15/18.
 */

public class Neighbour extends Contact {
    @SerializedName("distance")
    private float distance;
    @SerializedName("custid")
    private String id;

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public String getId() {
        return id;
    }
}
