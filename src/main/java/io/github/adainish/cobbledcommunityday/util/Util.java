package io.github.adainish.cobbledcommunityday.util;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.DiscordAccount;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Util
{
    public static MinecraftServer server = CobbledCommunityDay.getServer();

    public static DiscordAccount getDiscordAccount(long id) {
        return CobbledCommunityDay.wrapper.getDiscordAccountData().get(id);
    }
    private static final MinecraftServer SERVER = server;

    public static final long DAY_IN_TICKS = 1728000;
    public static final long DAY_IN_MILLIS = 86400000;
    public static final long HOUR_IN_MILLIS = 3600000;
    public static final long MINUTE_IN_MILLIS = 60000;
    public static final long SECOND_IN_MILLIS = 1000;
    public static final long HALF_AN_HOUR_IN_MILLIS = 1800000;


    public static void broadcast(String msg) {
        for (ServerPlayer pl:server.getPlayerList().getPlayers()) {
            send(pl, msg);
        }
    }

    public static void send(CommandSource sender, String message) {
        sender.sendSystemMessage(Component.literal((message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }

    public static String getTimeString(long l) {
        long days = l / DAY_IN_MILLIS;
        long hours = (l % DAY_IN_MILLIS) / HOUR_IN_MILLIS;
        long minutes = (l % HOUR_IN_MILLIS) / MINUTE_IN_MILLIS;
        long seconds = (l % MINUTE_IN_MILLIS) / SECOND_IN_MILLIS;
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
    }
}
