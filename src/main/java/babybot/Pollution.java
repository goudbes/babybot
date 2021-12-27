package babybot;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.AirQualityIndex;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.exception.NoDataFoundException;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.air.pollution.AirPollutionDetails;
import com.github.prominence.openweathermap.api.model.air.pollution.AirPollutionRecord;
import com.github.prominence.openweathermap.api.model.weather.Location;
import com.github.prominence.openweathermap.api.model.weather.Weather;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            return Helpers.handleError("Could not validate input for city.", ErrorResponse.POLLUTION.toString());
        }

        String apiKey = Helpers.getApiKey(ErrorResponse.POLLUTION.toString());
        OpenWeatherMapClient openWeatherClient = new OpenWeatherMapClient(apiKey);

        Weather weather;

        try {
            weather = openWeatherClient
                    .currentWeather()
                    .single()
                    .byCityName(city)
                    .language(Language.NORWEGIAN)
                    .unitSystem(UnitSystem.METRIC)
                    .retrieve()
                    .asJava();
        } catch (NoDataFoundException e) {
            return Helpers.handleError("No data was found for city " + city, ErrorResponse.POLLUTION.toString());
        }

        Location location = weather.getLocation();

        final AirPollutionDetails airPollutionDetails = openWeatherClient
                .airPollution()
                .current()
                .byCoordinate(Coordinate.of(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude()))
                .retrieve()
                .asJava();

        List<AirPollutionRecord> airPollutionData = airPollutionDetails.getAirPollutionRecords();
        switch (message[0]) {
            case "?air":
                return airPollutionData.get(0).toString();
            case "?luft":
                return prepareAirPollutionResponse(location.getName(), airPollutionData.get(0));
            default:
                return Helpers.handleError("Wrong input format.", ErrorResponse.POLLUTION.toString());
        }

    }

    /**
     * Air pollution response in new norwegian (nynorsk)
     * @param city location name
     * @param data air pollution record
     * @return response with air pollution data in new norwegian (nynorsk)
     */
    private static String prepareAirPollutionResponse(String city, AirPollutionRecord data) {
        final String BLANK_SPACE = " ";
        final String POLLUTION_MEASURE = " μg/m^3;";
        StringBuilder response = new StringBuilder("Luftforureining for staden").append(BLANK_SPACE).append(city);

        LocalDateTime dateTime = data.getForecastTime();
        String dateTimeData;
        if (dateTime != null) {
            DateTimeFormatter dateTimeFormatter;
            try {
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                dateTimeData = dateTime.format(dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeException e) {
                return Helpers.handleError("Failed on date time retrieval: " + e.getMessage(), ErrorResponse.POLLUTION.toString());
            }
            response.append(BLANK_SPACE).append(dateTimeData);
        }

        AirQualityIndex airQualityIndex = data.getAirQualityIndex();
        if (airQualityIndex != null) {
            response.append(BLANK_SPACE)
                    .append(("Luftkvalitetsindeks:"))
                    .append(BLANK_SPACE)
                    .append(airQualityIndex);
        }

        response.append(BLANK_SPACE)
                .append("Konsentrasjonar:")
                .append(BLANK_SPACE);

        Double carbonMonoxide = data.getCarbonMonoxide();
        if (carbonMonoxide != null) {
            response.append("Karbonmonoksid (CO)=")
                    .append(carbonMonoxide)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double nitrogenMonoxide = data.getNitrogenMonoxide();
        if (nitrogenMonoxide != null) {
            response.append("Nitrogenmonoksid (NO)=")
                    .append(nitrogenMonoxide)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double nitrogenDioxide = data.getNitrogenDioxide();
        if (carbonMonoxide != null) {
            response.append("Nitrogendioksid (NO2)=")
                    .append(nitrogenDioxide)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double ozone = data.getOzone();
        if (ozone != null) {
            response.append("Ozon (O3)=")
                    .append(ozone)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double sulphurDioxide = data.getSulphurDioxide();
        if (sulphurDioxide != null) {
            response.append("Svoveldioksid (SO2)=")
                    .append(sulphurDioxide)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double fineParticlesMatter = data.getFineParticlesMatter();
        if (fineParticlesMatter != null) {
            response.append("Ultrafine partiklar (PM2.5)=")
                    .append(fineParticlesMatter)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double coarseParticulateMatter = data.getCoarseParticulateMatter();
        if (coarseParticulateMatter != null) {
            response.append("Grove partiklar (PM10)=")
                    .append(coarseParticulateMatter)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        Double ammonia = data.getAmmonia();
        if (ammonia != null) {
            response.append("Ammoniakk (NH3)=")
                    .append(ammonia)
                    .append(POLLUTION_MEASURE)
                    .append(BLANK_SPACE);
        }

        return response.toString();
    }
}
