package babybot;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.exception.NoDataFoundException;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.aksingh.owmjapis.CurrentWeather;

import org.json.JSONException;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;


/**
 * File: Actions
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

class WeatherForecast {

    /**
     * Setting up open weather client based on location
     * @param message input message
     * @return response with weather data
     */
    static String getWeather(String[] message) {
        String location = Helpers.validateInput(message);
        if (location == null) {
            return Helpers.handleError("Could not validate input.", ErrorResponse.WEATHER.toString());
        }

        String apiKey = Helpers.getApiKey(ErrorResponse.WEATHER.toString());

        OpenWeatherMapClient openWeatherClient = new OpenWeatherMapClient(apiKey);
        Weather weather;

        try {
            weather = openWeatherClient
                    .currentWeather()
                    .single()
                    .byCityName(location)
                    .language(Language.NORWEGIAN)
                    .unitSystem(UnitSystem.METRIC)
                    .retrieve()
                    .asJava();
        } catch (NoDataFoundException e) {
            return Helpers.handleError("No data was found for location " + location, ErrorResponse.POLLUTION.toString());
        }

        switch (message[0]) {
            case "?ver":
                return getWeatherNewNorwegian(message);
            case "?weather":
                return weather.toString();
            default:
                return Helpers.handleError("Wrong input format.", ErrorResponse.WEATHER.toString());
        }
    }

    /**
     * Weather forecast for a specific city in New Norwegian (Nynorsk)
     *
     * @param msg input message
     * @return weather forecast in New Norwegian (Nynorsk)
     */
    static String getWeatherNewNorwegian(String[] msg) {
        //TODO: Rewrite this using OpenWeatherMapClient
        String city = Helpers.validateInput(msg);
        if (city == null) {
            return Helpers.handleError("Could not validate input.", ErrorResponse.WEATHER.toString());
        }
        final String DEGREE = "\u00b0";

        String apiKey = Helpers.getApiKey(ErrorResponse.WEATHER.toString());
        OpenWeatherMap owm = new OpenWeatherMap(apiKey);

        CurrentWeather cwd;
        try {
            cwd = owm.currentWeatherByCityName(city);
        } catch (IOException | JSONException e) {
            return Helpers.handleError("Could not fetch current weather for city" + city, ErrorResponse.WEATHER.toString());
        }

        String response = "V\u00EArvarsel for: ";
        DecimalFormat df = new DecimalFormat("#.00");

        if (cwd != null && cwd.isValid()) {

            if (cwd.hasCityName()) {
                response += cwd.getCityName();
            }

            float lon, lat;
            if (cwd.hasCoordInstance() && cwd.getCoordInstance().hasLatitude() && cwd.getCoordInstance().hasLongitude()) {
                lon = cwd.getCoordInstance().getLongitude();
                lat = cwd.getCoordInstance().getLatitude();
                String formattedLocation = Helpers.getFormattedLocationInDegree(lat, lon);

                response += " Koordinatar: " + formattedLocation;
            }

            float temperature, humidity, pressure;

            if (cwd.hasMainInstance()) {
                CurrentWeather.Main main = cwd.getMainInstance();
                if (main.hasTemperature()) {
                    temperature = ((main.getTemperature() - 32) * 5) / 9;
                    response += " Temperatur: " + df.format(temperature) + " " + DEGREE + "C";

                }
                if (main.hasPressure()) {
                    pressure = main.getPressure();
                    response += " Trykk: " + pressure + " hPa";
                }

                if (main.hasHumidity()) {
                    humidity = main.getHumidity();
                    response += " Luftfukt: " + humidity + "%";
                }
            }

            if (cwd.hasWeatherInstance()) {
                CurrentWeather.Weather weather = cwd.getWeatherInstance(0);
                if (weather.hasWeatherDescription()) {
                    String weatherDescription = weather.getWeatherDescription();
                    response += " Detaljert: " + weatherDescription;
                }
            }

            if (cwd.hasCloudsInstance()) {
                CurrentWeather.Clouds clouds = cwd.getCloudsInstance();
                if (clouds.hasPercentageOfClouds()) {
                    float cloud = clouds.getPercentageOfClouds();
                    response += " Skydekke: " + cloud + "%";
                }
            }

            if (cwd.hasRainInstance()) {
                CurrentWeather.Rain rains = cwd.getRainInstance();
                if (rains.hasRain()) {
                    float rain = rains.getRain();
                    response += " Nedb\u00F8r: " + rain;
                }
            }

            float windSpeed;
            String windDirection;
            if (cwd.hasWindInstance()) {
                CurrentWeather.Wind wind = cwd.getWindInstance();
                if (wind.hasWindSpeed() && wind.hasWindDegree()) {
                    windSpeed = wind.getWindSpeed();
                    double w = wind.getWindDegree();
                    w = Math.round(w * 100) / 100.0;
                    windDirection = Helpers.getWindDirect(w);
                    response += " Vind: " + windSpeed + " m/s fr\u00E5 " + windDirection;
                }
            }

            if (cwd.hasSysInstance()) {
                CurrentWeather.Sys sys = cwd.getSysInstance();
                if (sys.hasCountryCode()) {
                    String countryCode = sys.getCountryCode();
                    response += " Landskode: " + countryCode;
                }
                if (sys.hasSunriseTime()) {
                    Date sunriseTime = sys.getSunriseTime();
                    response += " Soloppgang: " + sunriseTime;
                }

                if (sys.hasSunsetTime()) {
                    Date sunsetTime = sys.getSunsetTime();
                    response += " Solnedgang: " + sunsetTime;
                }
            }

            return response;
        }
        return Helpers.handleError("Current weather data is not valid.", ErrorResponse.WEATHER.toString());
    }


}