package babybot;

public abstract class ExtremeLocation {
    private int climate;
    private String name;
    private int wmoid;
    private double value;
    private String stationType;

    public ExtremeLocation(int climate, String name, double value, String stationType, int wmoid) {
        this.climate = climate;
        this.name = name;
        this.value = value;
        this.stationType = stationType;
        this.wmoid = wmoid;
    }

    public ExtremeLocation(int climate, String name, double value, String stationType) {
        this.climate = climate;
        this.name = name;
        this.value = value;
        this.stationType = stationType;
        this.wmoid = 0;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate(int climate) {
        this.climate = climate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWmoid() {
        return wmoid;
    }

    public void setWmoid(int wmoid) {
        this.wmoid = wmoid;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

}
