package io.github.adainish.cobbledcommunityday.obj;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.ReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.ArrayList;
import java.util.List;

public class Bot
{
    private String serverID;

    public Bot() {}

    public JDA jda;

    public void logoutBot() {
        if (jda == null)
            return;
        List<Object> list = new ArrayList<>(jda.getRegisteredListeners());
        list.forEach(listener -> {
            jda.removeEventListener(listener);
        });
        jda.shutdown();
    }

    public Guild getDesiredChannelGuild()
    {
        return CobbledCommunityDay.bot.jda.getGuildById(CobbledCommunityDay.config.guildID);
    }

    public void loginBot(String args) throws Exception {
        if (args.isEmpty())
            return;
        try {

            JDABuilder jdaBuilder = JDABuilder.createDefault(args);
            jdaBuilder.setStatus(OnlineStatus.ONLINE);
            jdaBuilder.addEventListeners(new ReactionListener());

            this.jda = jdaBuilder.build();
            jda.awaitReady();

        } catch (Exception e) {
            CobbledCommunityDay.getLog().error(e.getMessage());
            throw new Exception("Failed to log in");
        }

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
