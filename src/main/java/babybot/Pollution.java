package babybot;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.air.pollution.AirPollutionDetails;
import com.github.prominence.openweathermap.api.model.air.pollution.AirPollutionRecord;
import com.github.prominence.openweathermap.api.model.weather.Location;
import com.github.prominence.openweathermap.api.model.weather.Weather;

import java.util.List;

public class Pollution {

    /**
     * Provides air pollution details for a specific city
     *
     * @param message message with ?air prefix and city name following it
     * @return air pollution data
     */
    public static String getPollution(String[] message) {
        String city = Helpers.validateInput(message);
        if (city == null) {
            return Helpers.handleError("Could not validate input for city.", "Could not fetch weather data.");
        }
        final String errorResponse = "Could not fetch air pollution data for " + city;

        String apiKey = Helpers.getApiKey(errorResponse);
        OpenWeatherMapClient openWeatherClient = new OpenWeatherMapClient(apiKey);

        final Weather weather = openWeatherClient
                .currentWeather()
                .single()
                .byCityName(city)
                .language(Language.NORWEGIAN)
                .unitSystem(UnitSystem.METRIC)
                .retrieve()
                .asJava();

        Location location = weather.getLocation();

        final AirPollutionDetails airPollutionDetails = openWeatherClient
                .airPollution()
                .current()
                .byCoordinate(Coordinate.of(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude()))
                .retrieve()
                .asJava();

        List<AirPollutionRecord> airPollutionData = airPollutionDetails.getAirPollutionRecords();
        return airPollutionData.get(0).toString();
    }
}
