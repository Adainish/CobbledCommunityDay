package io.github.adainish.cobbledcommunityday.obj;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import org.apache.logging.log4j.Level;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.server.Server;

import java.net.MalformedURLException;
import java.net.URL;

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
        Channel channel = CobbledCommunityDay.bot.api.getChannelById(CobbledCommunityDay.config.channelID).orElse(null);
        if (channel == null)
            return;

        if (channel.asServerChannel().isPresent()) {
            Server server = channel.asServerChannel().get().getServer();
            CustomEmojiBuilder builder = new CustomEmojiBuilder(server);
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
                builder.setImage(url);
            } catch (Exception e) {
                CobbledCommunityDay.getLog().log(Level.WARN, e);
            }

            builder.setName(name);
            builder.setAuditLogReason("Community day Emoji");
            builder.create().join();
            setEmojiName(name);
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
