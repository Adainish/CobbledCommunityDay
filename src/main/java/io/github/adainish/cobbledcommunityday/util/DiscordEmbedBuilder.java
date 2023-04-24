package io.github.adainish.cobbledcommunityday.util;

import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.CommunityPokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class DiscordEmbedBuilder
{
    public static MessageCreateAction sendPokemonEmbedToChannel(String channelID, String title, String msg, CommunityPokemon pokemon) {
        Guild guild = CobbledCommunityDay.bot.getDesiredChannelGuild();
        if (guild == null) {
            CobbledCommunityDay.getLog().error("Unable to return the configured Discord Guild to work with!");
            return null;
        }
        TextChannel channel = guild.getTextChannelById(channelID);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN, "A channel returned as non existent while attempting to send out a message");
            return null;
        }
        EmbedBuilder embed = generatePokemonEmbed(pokemon.getPokemon());
        embed.setTitle(title);
        embed.setDescription(msg);
        try {
            return channel.sendMessageEmbeds(embed.build());
        } catch (Exception e) {
            CobbledCommunityDay.getLog().warn("Failed to send message...");
        }
        return null;
    }


    public static void sendAnnouncementMessageToChannel(String channelID, String title, String msg) {
        Guild guild = CobbledCommunityDay.bot.getDesiredChannelGuild();
        if (guild == null)
        {
            CobbledCommunityDay.getLog().error("Unable to return the configured Discord Guild to work with!");
            return;
        }
        TextChannel channel = guild.getTextChannelById(channelID);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN, "A channel returned as non existent while attempting to send out a message");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(msg);
        embed.setColor(Color.RED);
        try {
            channel.sendMessageEmbeds(embed.build()).submit();
        } catch (Exception e)
        {
            CobbledCommunityDay.getLog().warn("Failed to send message...");
        }
    }


    public static void sendBotMessageToChannel(String channelID, String title, String msg) {
        Guild guild = CobbledCommunityDay.bot.getDesiredChannelGuild();
        if (guild == null)
        {
            CobbledCommunityDay.getLog().error("Unable to return the configured Discord Guild to work with!");
            return;
        }

        TextChannel channel = guild.getTextChannelById(channelID);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN, "A channel returned as non existent while attempting to send out a message");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(msg);
        embed.setColor(Color.RED);
        try {
            channel.sendMessageEmbeds(embed.build()).submit();
        } catch (Exception e)
        {
            CobbledCommunityDay.getLog().warn("Failed to send message...");
        }

    }

    public static EmbedBuilder generatePokemonEmbed(Pokemon pokemon) {
        EmbedBuilder builder = new EmbedBuilder();

        String link = null;


        builder.addField("Pokemon: ", pokemon.getSpecies().getName(), true);

        if (pokemon.getShiny()) {
            link = "https://play.pokemonshowdown.com/sprites/xyani-shiny/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
        } else {
            link = "https://play.pokemonshowdown.com/sprites/xyani/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
        }

        builder.setImage(link);

        return builder;
    }

}
