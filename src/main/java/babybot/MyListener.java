package babybot;

import org.json.JSONException;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import org.pircbotx.Configuration.*;
import org.pircbotx.hooks.events.OpEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * File: MyListener
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

public class MyListener extends ListenerAdapter {

    public static void main(String[] args) {
        Configuration config = configure();
        PircBotX bot = new PircBotX(config);
        try {
            bot.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure bot
     *
     * @return configuration
     * @throws IOException
     */
    private static Configuration configure() {
        JSONObject config = null;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("config.json")) {
            config = (JSONObject) jsonParser.parse(reader);
            System.out.println(config);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        if (config == null)
            throw new NullPointerException("Could not parse configuration file.");

        Builder builder = new Builder();
        builder.setName((String) config.get("name"));
        builder.addServer((String) config.get("server"));
        builder.setLogin((String) config.get("login"));
        builder.setRealName((String) config.get("realname"));
        builder.addAutoJoinChannel((String) config.get("autojoin"));
        builder.setAutoSplitMessage(true);
        builder.setEncoding(StandardCharsets.UTF_8);
        builder.setVersion((String) config.get("version"));
        builder.addListener(new MyListener());
        return builder.buildConfiguration();
    }

    @Override
    public void onMessage(MessageEvent event) throws JSONException {

        String message = event.getMessage().toLowerCase();
        String[] msg = message.split("\\s+");

        switch (msg[0]) {
            case "?time":
                event.respond(Time.getTime());
                break;
            case "?ver":
            case "?weather":
                event.respond(WeatherForecast.getWeather(msg));
                break;
            case "?luft":
            case "?air":
                event.respond(Pollution.getPollution(msg));
                break;
            default:
                break;
        }
    }

    @Override
    public void onJoin(JoinEvent event) {
        Voice.giveVoice(event);
    }

    @Override
    public void onOp(OpEvent event) {
        if (event.getRecipient() == event.getChannel().getBot().getUserBot()) {
            Voice.giveVoiceAfterJoin(event);
        }
    }

}