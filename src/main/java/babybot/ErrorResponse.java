package babybot;

/**
 * Enum containing error response depending on the context
 */
public enum ErrorResponse {
    POLLUTION("Could not fetch air pollution data."),
    WEATHER("Could not fetch weather data.");

    public final String label;

    private ErrorResponse(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
