package io.github.adainish.cobbledcommunityday.obj;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.ReactionListener;
import io.github.adainish.cobbledcommunityday.util.Util;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bot
{
    private String serverID;

    public Bot() {}

    public JDA api;

    public void logoutBot() {
        if (api == null)
            return;
        List<Object> list = new ArrayList<>();
        api.getRegisteredListeners().forEach(listener -> {
            list.add(listener);
        });
        list.forEach(listener -> {
            api.removeEventListener(listener);
        });
        api.shutdown();
    }

    public void loginBot(String args) throws Exception {
        if (args.isEmpty())
            return;
        try {
            this.api = JDABuilder.createDefault(args)
                    .setToken(args)
                    .enableIntents(Arrays.stream(GatewayIntent.values()).toList())
                    .build();
        } catch (Exception e) {
            CobbledCommunityDay.getLog().error(e.getMessage());
            throw new Exception("Failed to log in");
        }
        if (this.api != null) {
            CobbledCommunityDay.getLog().log(Level.WARN,"Community Day Bot has launched successfully!");
            // Print the invite url of your bot
            CobbledCommunityDay.getLog().log(Level.WARN,"You can invite the bot by using the following url: " + generateInvite(api));
            initVotingListener(this.api);
        }
    }

    public void initVotingListener(JDA api) {
        api.addEventListener(new ReactionListener());
    }



    public String generateInvite(JDA api) {
        return api.getInviteUrl(Permission.ADMINISTRATOR);
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
}
