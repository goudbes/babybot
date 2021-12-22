package babybot;

import net.aksingh.owmjapis.OpenWeatherMap;
import net.aksingh.owmjapis.CurrentWeather;
import org.pircbotx.Configuration;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.output.OutputChannel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * File: Actions
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

class Actions {

    /**
     * Time and date
     * Example: ?time
     *
     * @return time and date
     */
    static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zzzz");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Places in Norway with most precipitation, max and min temperatures
     * Data taken from: https://api.met.no/index_no.html
     */
    static void getExtremes(MessageEvent event) throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL("https://api.met.no/weatherapi/extremeswwc/1.2/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("Connection established...");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(connection.getInputStream());
            NodeList nodeList = document.getElementsByTagName("maximumPrecipitations");

            List<LocationPrecipitation> locations = Func.getMaxPrecipitation(document);
            List<LocationTemperature> temperatures = Func.getTemperatures(document);
            event.respond("Nedb\u00F8r:\n");
            for (int i = 0; i < 1; i++) {
                event.respond(locations.get(i).toString());
            }
            event.respond("H\u00F8gste temperatur:\n");
            for (int i = 0; i < 1; i++) {
                event.respond(temperatures.get(i).toString());
            }

            event.respond("L\u00E5gaste temperatur:\n");
            for (int i = temperatures.size() - 1; i > temperatures.size() - 2; i--) {
                event.respond(temperatures.get(i).toString());
            }

        } else {
            event.respond("Couldn't establish connection.");
        }
    }


    /**
     * Weather forecast for a specific city
     *
     * @param msg command ?weather and the name of the city
     * @return weather forecast
     * @throws IOException
     */
    static String getWeather(String[] msg) throws IOException {
        if (msg.length < 2) {
            return "Wrong parameters";
        }
        final String DEGREE = "\u00b0";
        StringBuilder city = new StringBuilder();

        for (int i = 1; i < msg.length; i++) {
            if (city.toString().equalsIgnoreCase("")) {
                city.append(msg[i]);
            } else
                city.append(" ").append(msg[i]);
        }

        OpenWeatherMap owm = new OpenWeatherMap("");
        owm.setApiKey("7d78597ea1f8d3392016fc45014dc5a9");
        CurrentWeather cwd = owm.currentWeatherByCityName(city.toString());
        String response = "V\u00EArvarsel for: ";
        DecimalFormat df = new DecimalFormat("#.00");

        if (cwd.isValid()) {

            if (cwd.hasCityName()) {
                response += cwd.getCityName();
            }

            float lon, lat;
            if (cwd.hasCoordInstance() && cwd.getCoordInstance().hasLatitude() && cwd.getCoordInstance().hasLongitude()) {
                lon = cwd.getCoordInstance().getLongitude();
                lat = cwd.getCoordInstance().getLatitude();
                String formattedLocation = Func.getFormattedLocationInDegree(lat, lon);

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
                    windDirection = Func.getWindDirect(w);
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
        return "Not found";
    }


    /**
     * The bot gives voices to the users who join the channel
     *
     * @param event Join event
     */
    static void giveVoice(JoinEvent event) {
        Set<User> normalUsers = event.getChannel().getNormalUsers();
        Configuration.BotFactory botFactory = event.getBot().getConfiguration().getBotFactory();
        if (event.getChannel().isOp(event.getBot().getUserBot()) && event.getUser() != null) {
            if ((!(event.getChannel().hasVoice(event.getUser()))) && (normalUsers.contains(event.getUser()))) {
                OutputChannel outputChannel = botFactory.createOutputChannel(event.getBot(), event.getChannel());
                outputChannel.voice(event.getUser());
            }
        }
    }

    /**
     * The bot is voicing the users after he is given an OP
     *
     * @param event OpEvent on the channel
     */
    static void giveVoiceAfterJoin(OpEvent event) {
        Set<User> normalUsers = event.getChannel().getNormalUsers();
        Configuration.BotFactory botFactory = event.getBot().getConfiguration().getBotFactory();
        OutputChannel outputChannel = botFactory.createOutputChannel(event.getBot(), event.getChannel());
        if (event.getChannel().isOp(event.getBot().getUserBot())) {
            for (User u : normalUsers) {
                outputChannel.voice(u);
            }
        }
    }
}