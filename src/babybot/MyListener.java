package babybot;
import org.json.JSONException;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.*;
import org.pircbotx.Configuration.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * File: MyListener
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

public class MyListener extends ListenerAdapter {


    private static Configuration configure() throws IOException {
        //Configure what we want our bot to do
        HashMap<String,String> config;
        config = readConfigFile();
        Builder builder = new Builder();
        builder.setName(config.get("Name"));
        builder.addServer(config.get("Server"));
        builder.setLogin(config.get("Login"));
        builder.setRealName(config.get("Realname"));
        builder.addAutoJoinChannel(config.get("Autojoin"));
        builder.setAutoSplitMessage(true);
        builder.setEncoding(StandardCharsets.UTF_8);
        builder.setVersion(config.get("Version"));
        builder.addListener(new MyListener());
        return builder.buildConfiguration();
    }

    private static HashMap<String,String> readConfigFile() {
        HashMap<String,String> content = new HashMap<>();
        File file = new File("config.txt"); //for ex foo.txt
        try {
            Scanner in = new Scanner(file);
            while(in.hasNextLine()) {
                String[] line = in.nextLine().split("\\s+");
                content.put(line[0],line[1]);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void main(String[] args) throws Exception {
        Configuration config = configure();
        PircBotX bot = new PircBotX(config);

        //Connect to the server
        bot.startBot();

    }

    @Override
    public void onMessage(MessageEvent event) throws IOException, JSONException, ParserConfigurationException, SAXException {

        String message = event.getMessage().toLowerCase();
        String[] msg = message.split("\\s+");
        String response;

        switch (msg[0]) {
            case "?time":
                response = Actions.getTime();
                event.respond(response);
                break;
            case "?weather":
                response = Actions.getWeather(msg);
                event.respond(response);
                break;
            case "?extremes":
                Actions.getExtremes(event);
            default:
                break;

        }
    }

    @Override
    public void onJoin(JoinEvent event) {
        Actions.giveVoice(event);
    }

    @Override
    public void onOp(OpEvent event) {
        if (event.getRecipient() == event.getChannel().getBot().getUserBot()) {
            Actions.giveVoiceAfterJoin(event);
        }
    }
}