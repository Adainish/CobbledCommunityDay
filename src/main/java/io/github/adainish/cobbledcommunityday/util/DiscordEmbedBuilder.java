package io.github.adainish.cobbledcommunityday.util;

import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.CommunityPokemon;
import org.apache.logging.log4j.Level;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
public class DiscordEmbedBuilder
{
    public static void sendPokemonEmbedToChannel(String channelID, String title, String msg, CommunityPokemon pokemon) {
        TextChannel channel = CobbledCommunityDay.bot.api.getTextChannelById(channelID).orElse(null);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN,"A channel returned as non existent while attempting to send out a message");
            return;
        }

        EmbedBuilder embed = generatePokemonEmbed(pokemon.getPokemon());
        embed.setTitle(title);
        embed.setDescription(msg);
        channel.sendMessage(embed);
    }


    public static void sendAnnouncementMessageToChannel(String channelID, String title, String msg) {
        TextChannel channel = CobbledCommunityDay.bot.api.getTextChannelById(channelID).orElse(null);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN, "A channel returned as non existent while attempting to send out a message");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(msg);
        embed.setColor(Color.RED);
        channel.sendMessage("@everyone", embed);
    }

    public static void sendBotMessageToChannel(String channelID, String title, String msg) {
        TextChannel channel = CobbledCommunityDay.bot.api.getTextChannelById(channelID).orElse(null);

        if (channel == null) {
            CobbledCommunityDay.getLog().log(Level.WARN, "A channel returned as non existent while attempting to send out a message");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(msg);
        embed.setColor(Color.RED);
        channel.sendMessage(embed);
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
