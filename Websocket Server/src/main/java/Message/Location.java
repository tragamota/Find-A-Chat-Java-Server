package Message;

import com.owlike.genson.annotation.JsonCreator;

public class Location {
    private double langitude, longitude;

    @JsonCreator
    public Location(double langitude, double longitude) {
        this.langitude = langitude;
        this.longitude = longitude;
    }

    public double getLangitude() {
        return langitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
