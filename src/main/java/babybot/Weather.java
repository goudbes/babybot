package babybot;

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

class Weather {


    /**
     * Weather forecast for a specific city
     *
     * @param msg command ?weather and the name of the city
     * @return weather forecast
     */
    static String getWeather(String[] msg) {

        String city = Helpers.validateInput(msg);
        if (city == null) {
            return Helpers.handleError("Could not validate input for city.", "Could not fetch weather data.");
        }
        final String errorResponse = "Could not fetch weather data for " + city;
        final String DEGREE = "\u00b0";

        String apiKey = Helpers.getApiKey(errorResponse);
        OpenWeatherMap owm = new OpenWeatherMap(apiKey);

        CurrentWeather cwd = null;
        try {
            cwd = owm.currentWeatherByCityName(city);
        } catch (IOException | JSONException e) {
            return Helpers.handleError("Could not fetch current weather for city" + city, errorResponse);
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
                    double w = (double) wind.getWindDegree();
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
        return Helpers.handleError("Current weather data is not valid.", errorResponse);
    }


}