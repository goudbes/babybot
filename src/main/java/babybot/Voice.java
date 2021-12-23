package babybot;

import org.pircbotx.Configuration;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.output.OutputChannel;

import java.util.Set;

/**
 * File: Voice
 * Author: Goudbes
 * Created: 2021.12.23, 12.04
 */

public class Voice {

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
