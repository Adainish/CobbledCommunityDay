package io.github.adainish.cobbledcommunityday.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.Adapters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config
{
    public String botToken;
    public String channelID;
    public String broadCast;
    public int daysUntil;
    public int waitingTime;
    public int votingTime;

    public int shinyRate;
    public int ivsBuff;
    public int haRate;
    public int spawnRate;
    public int maxPokemon;
    public List<String> blackListedSpecies = new ArrayList<>();
    public Config()
    {
        this.botToken = "";
        this.channelID = "";
        this.daysUntil = 1;
        this.votingTime = 2;
        this.shinyRate = 10;
        this.haRate = 10;
        this.ivsBuff = 5;
        this.spawnRate = 1;
        this.waitingTime = 3;
        this.maxPokemon = 5;
        this.blackListedSpecies = new ArrayList<>();
        this.broadCast = "&b&lToday is Community Day! The Pokemon %pkmn% will appear more often. They may have their hidden ability, be shiny or have higher IV stats than usual!";
    }

    public static void writeConfig()
    {
        File dir = CobbledCommunityDay.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        Config config = new Config();
        try {
            File file = new File(dir, "config.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledCommunityDay.getLog().warn(e);
        }
    }

    public static Config getConfig()
    {
        File dir = CobbledCommunityDay.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "config.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledCommunityDay.getLog().error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, Config.class);
    }
}
