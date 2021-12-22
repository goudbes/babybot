package babybot;

public class Location {
    private int id;
    private String name;
    private double lon;
    private double lat;
    private String countryCode;
    private int timeZone;

    public Location() {

    }

    public Location(int id, String name, double lon, double lat, String countryCode, int timeZone) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.countryCode = countryCode;
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
