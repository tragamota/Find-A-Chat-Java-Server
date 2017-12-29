package Message;

public class Location {
    private double langitude, longitude;

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
