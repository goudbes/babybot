package babybot;

public class LocationTemperature extends ExtremeLocation {

    static final String unit = " \u00B0C";

    public LocationTemperature(int climate, String name, double value, String stationType, int wmoid) {
        super(climate, name, value, stationType, wmoid);
    }

    public LocationTemperature(int climate, String name, double value, String stationType) {
        super(climate, name, value, stationType);
    }

    public String toString() {
        return " Stad: " + getName() + " Temperatur: " + getValue() + unit +
                " Stasjonstype: " + getStationType();
    }
}
