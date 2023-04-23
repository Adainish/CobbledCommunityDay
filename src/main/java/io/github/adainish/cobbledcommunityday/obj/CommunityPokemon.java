package io.github.adainish.cobbledcommunityday.obj;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.storage.EmojiStorage;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.managers.CustomEmojiManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CommunityPokemon
{
    private String pokemonName;
    private String emojiName = "";
    private int votes = 0;

    public CommunityPokemon(Pokemon pokemon) {
        if (pokemon != null) {
            setPokemonName(pokemon.getSpecies().getName());
        }
        scrapeCustomEmote();
    }

    public Pokemon getPokemon() {
        Species species = null;
        if (PokemonSpecies.INSTANCE.getByName(pokemonName) != null)
            species = PokemonSpecies.INSTANCE.getByName(pokemonName);
        else species = PokemonSpecies.INSTANCE.getByName("magikarp");
        return species.create(100);
    }

    public void scrapeCustomEmote() {
        TextChannel channel = CobbledCommunityDay.bot.api.getTextChannelById(CobbledCommunityDay.config.channelID);
        if (channel == null)
            return;

            String link;
            Pokemon pokemon = getPokemon();
            if (pokemon.getShiny()) {
                link = "https://play.pokemonshowdown.com/sprites/xyani-shiny/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
            } else {
                link = "https://play.pokemonshowdown.com/sprites/xyani/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
            }
            String name = "communityday_" + pokemon.getSpecies().getName();
            URL url = null;
            try {
                url = new URL(link);
            } catch (MalformedURLException e) {
                try {
                    url = new URL("https://play.pokemonshowdown.com/sprites/substitutes/gen5/substitute.png");
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
            try {
            } catch (Exception e) {
                CobbledCommunityDay.getLog().log(Level.WARN, e);
            }

        try {
            FileUtils.copyURLToFile(
                    url,
                    new File(CobbledCommunityDay.getEmojiStorage(), pokemon.getSpecies().getName().toLowerCase() + ".gif"),
                    100,
                    100);

        } catch (Exception e) {

        }


        try {
            File f = EmojiStorage.getEmojiFile(pokemon.getSpecies().getName().toLowerCase());
            if (f.exists()) {
                Icon icon = Icon.from(f);
               AuditableRestAction<RichCustomEmoji> restAction = channel.getGuild().createEmoji(name, icon, (Role) channel.getJDA().getRoles());
               restAction.reason("Community Day emoji scraping");
               restAction.submit();
            } else {
                CobbledCommunityDay.getLog().error("Failed to retrieve emoji for %species%".replace("%species%", pokemon.getSpecies().getName()));
            }
        } catch (Exception e) {

        }
    }


    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void increaseVotes(int amount) {
        this.votes += amount;
    }


    public String getEmojiName() {
        return emojiName;
    }

    public void setEmojiName(String emojiName) {
        this.emojiName = emojiName;
    }
}
