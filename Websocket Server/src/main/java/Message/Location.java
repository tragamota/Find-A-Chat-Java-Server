package Message;

import com.owlike.genson.annotation.JsonCreator;

public class Location {
    private double latitude, longitude;

    @JsonCreator
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
