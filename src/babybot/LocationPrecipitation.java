package babybot;

public class LocationPrecipitation extends ExtremeLocation {

    static final String unit = " mm";

    public LocationPrecipitation(int climate, String name, double value, String stationType, int wmoid) {
        super(climate, name, value, stationType, wmoid);
    }

    public LocationPrecipitation(int climate, String name, double value, String stationType) {
        super(climate, name, value, stationType);
    }

    public String toString() {
        return " Stad: " + getName() + " Nedb\u00F8r: " + getValue() + unit +
                " Stasjonstype: " + getStationType();
    }
}
