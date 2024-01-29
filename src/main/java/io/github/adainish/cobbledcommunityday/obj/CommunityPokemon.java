package io.github.adainish.cobbledcommunityday.obj;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.storage.EmojiStorage;
import io.github.adainish.cobbledcommunityday.util.RandomHelper;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.minecraft.world.level.Level;

import java.io.*;
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

    public PokemonProperties getPokemonProperties() {
        PokemonProperties properties = new PokemonProperties();
        // set level between range of 10 and 40
        int randomLvL = RandomHelper.getRandomNumberBetween(10, 40);
        properties.setLevel(randomLvL);
        if (PokemonSpecies.INSTANCE.getByName(pokemonName.toLowerCase()) != null)
            properties.setSpecies(pokemonName.toLowerCase());
        else properties.setSpecies("magikarp");
        return properties;
    }

    public PokemonEntity getAndCreatePokemonEntity(Level level) {
        return getPokemonProperties().createEntity(level);
    }

    public Pokemon getPokemon() {
        return getPokemonProperties().create();
    }

    public void scrapeCustomEmote() {
        TextChannel channel = CobbledCommunityDay.bot.jda.getTextChannelById(CobbledCommunityDay.config.channelID);
        if (channel == null)
            return;

        Pokemon pokemon = getPokemon();
        String link;
        if (pokemon.getShiny()) {
                link = "https://play.pokemonshowdown.com/sprites/ani-shiny/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
            } else {
                link = "https://play.pokemonshowdown.com/sprites/ani/" + pokemon.getSpecies().getName().toLowerCase() + ".gif";
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
            byte[] b = new byte[1];
            URLConnection urlConnection = null;
                urlConnection = url.openConnection();


                urlConnection.connect();

            DataInputStream di = null;

                di = new DataInputStream(urlConnection.getInputStream());

            FileOutputStream fo = null;
            fo = new FileOutputStream(CobbledCommunityDay.getEmojiStorage() + "/%name%.gif".replace("%name%", pokemon.getSpecies().getName().toLowerCase()));

            while (-1 != di.read(b, 0, 1))
                fo.write(b, 0, 1);
            di.close();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            File f = EmojiStorage.getEmojiFile(pokemon.getSpecies().getName().toLowerCase());
            if (f.exists()) {
                Icon icon = Icon.from(f);
               AuditableRestAction<RichCustomEmoji> restAction = channel.getGuild().createEmoji(name, icon);
               restAction.reason("Community Day emoji scraping");
               restAction.submit();
               setEmojiName(name);
            } else {
                CobbledCommunityDay.getLog().error("Failed to retrieve emoji for %species%".replace("%species%", pokemon.getSpecies().getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
