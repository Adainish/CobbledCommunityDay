package io.github.adainish.cobbledcommunityday.storage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.Adapters;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;

import java.io.*;

public class CommunityDayStorage
{
    public static CommunityDayWrapper getWrapper() {
        File dir = CobbledCommunityDay.getStorage();
        File playerFile = new File(dir, "communityday.json");
        Gson gson = Adapters.PRETTY_MAIN_GSON;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(playerFile));
        } catch (FileNotFoundException e) {
            CobbledCommunityDay.getLog().error("Detected non-existing community day data, making new data file");
            return null;
        }

        return gson.fromJson(reader, CommunityDayWrapper.class);
    }

    public static void createCommunityDayWrapper() {
        File dir = CobbledCommunityDay.getStorage();
        dir.mkdirs();

        CommunityDayWrapper data = new CommunityDayWrapper();
        data.createNewCommunityDay();

        File file = new File(dir, "communityday.json");
        if (file.exists()) {
            CobbledCommunityDay.getLog().error("There was an issue generating the Community Day Data, already exists? Ending function");
            return;
        }

        Gson gson = Adapters.PRETTY_MAIN_GSON;
        String json = gson.toJson(data);

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException | NullPointerException e ) {
            e.printStackTrace();
        }
    }

    public static void saveCommunityDay(CommunityDayWrapper communityDay) {
        File dir = CobbledCommunityDay.getStorage();
        dir.mkdirs();

        File file = new File(dir, "communityday.json");
        Gson gson = Adapters.PRETTY_MAIN_GSON;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (reader == null) {
            CobbledCommunityDay.getLog().error("Something went wrong attempting to read the Community Day Data");
            return;
        }


        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(communityDay));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CobbledCommunityDay.wrapper = communityDay;
    }

    public static void deleteWrapper() {

    }
}
