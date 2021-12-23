package babybot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * File: Actions
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

class Helpers {

    /**
     * @param degree Wind degree
     * @return Wind direction
     */
    static String getWindDirect(double degree) {
        String[] directions = {"nord", "nordaust", "aust", "s\u00F8raust", "s\u00F8r", "s\u00F8rvest", "vest", "nordvest", "nord"};
        return directions[(int) Math.round(((degree % 360) / 45))];
    }

    /**
     * Formatting degrees
     * author: Akeshwar Jha
     * source: stackoverflow.com/a/38548560
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @return Formatted location in degrees
     */
    static String getFormattedLocationInDegree(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {
            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
        }
    }



    /**
     * Validate input for city name
     *
     * @param message input message
     * @return city
     */
    static String validateInput(String[] message) {
        if (message.length < 2) {
            return null;
        }
        StringBuilder city = new StringBuilder();

        for (int i = 1; i < message.length; i++) {
            if (city.toString().equalsIgnoreCase("")) {
                city.append(message[i]);
            } else
                city.append(" ").append(message[i]);
        }
        return city.toString();
    }

    static String handleError(String debugMessage, String errorResponse) {
        System.out.println(debugMessage);
        return errorResponse;
    }

    /**
     * Retrieve API key
     *
     * @return API key
     */
    static String getApiKey(String errorResponse) {
        final String key = "open_weather_map";

        JSONObject apiKeys = null;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("api_keys.json")) {
            apiKeys = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            return null;
        }

        if (apiKeys == null) {
            return handleError("Error retrieving API keys object", errorResponse);
        }

        if (!apiKeys.containsKey(key)) {
            return Helpers.handleError("API key was not found.", errorResponse);
        }
        return (String) apiKeys.get(key);
    }

}


