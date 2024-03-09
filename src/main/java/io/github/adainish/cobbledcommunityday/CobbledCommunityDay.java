package io.github.adainish.cobbledcommunityday;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.cobbledcommunityday.cmd.Command;
import io.github.adainish.cobbledcommunityday.config.Config;
import io.github.adainish.cobbledcommunityday.obj.Bot;
import io.github.adainish.cobbledcommunityday.storage.CommunityDayStorage;
import io.github.adainish.cobbledcommunityday.subscriptions.EventSubscriptions;
import io.github.adainish.cobbledcommunityday.task.AnnouncementCheck;
import io.github.adainish.cobbledcommunityday.task.BroadcastTask;
import io.github.adainish.cobbledcommunityday.task.CreationCheck;
import io.github.adainish.cobbledcommunityday.task.VotingCheck;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file

public class CobbledCommunityDay implements ModInitializer {

    public static CobbledCommunityDay instance;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobbledcommunityday";
    public static final String MOD_NAME = "CommunityDay";
    public static final String VERSION = "1.1.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";
    private static final Logger log = LogManager.getLogger(MOD_NAME);
    private static MinecraftServer server;
    private static File configDir;
    private static File storage;

    private static File emojiStorage;

    public static Config config;

    public static String token;
    public static Bot bot;
    public static CommunityDayWrapper wrapper;

    public static EventSubscriptions subscriptions;

    public CobbledCommunityDay() {
        instance = this;
    }

    public static org.apache.logging.log4j.Logger getLog() {
        return log;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobbledCommunityDay.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobbledCommunityDay.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        CobbledCommunityDay.storage = storage;
    }

    public static File getEmojiStorage() {
        return emojiStorage;
    }

    public static void setEmojiStorage(File emojiStorage) {
        CobbledCommunityDay.emojiStorage = emojiStorage;
    }

    @Override
    public void onInitialize() {
        commonSetup();
    }

    private void commonSetup() {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        initDirs();

        PlatformEvents.SERVER_STARTED.subscribe(Priority.NORMAL, t -> {
            setServer(t.getServer());
            //load data from config
            initConfigs();
            if (loginBot()) {
                log.warn("Bot creation appeared successfull, awaiting JDA initialisation");
            } else {
                log.warn("Could not log into the community bot, community day will not be active");
            }

            return Unit.INSTANCE;
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryaccess, environment) -> {
            log.warn("Registering commands for Community Day");
            dispatcher.register(Command.getCommand());
        });
    }

    public void startTasks()
    {
        log.warn("Starting tasks for Community Day");

        Task announcementRunnableTask = Task.builder().infinite().interval(20)
                .execute(new AnnouncementCheck())
                .build();

        Task broadCastTask = Task.builder().infinite().interval(20)
                .execute(new BroadcastTask())
                .build();

        Task creationRunnableTask = Task.builder().infinite().interval(20)
                .execute(new CreationCheck())
                .build();
        Task votingRunnableTask = Task.builder().infinite().interval(20)
                .execute(new VotingCheck())
                .build();

    }


    public void setOrCreateCommunityDay()
    {
        if (CommunityDayStorage.getWrapper() == null) {
            CommunityDayStorage.createCommunityDayWrapper();
        }
        wrapper = CommunityDayStorage.getWrapper();
        wrapper.wipeNonActiveEmotes();

    }

    public boolean loginBot() {
        token = config.botToken;

        bot = new Bot();
        if (token == null || token.isEmpty()) {
            log.error("There was an issue logging into the Bot, The Token was either null or empty!");
            return false;
        }
        try {
            bot.loginBot(token);
        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void logoutBot() {
        if (bot == null)
            return;
        bot.logoutBot();

    }


    public void initDirs() {
        setConfigDir(new File(FabricLoader.getInstance().getConfigDir() + "/CommunityDay/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
        setEmojiStorage(new File(getConfigDir(), "/emojistorage/"));
        getEmojiStorage().mkdirs();
    }

    public void initConfigs() {
        Config.writeConfig();
        config = Config.getConfig();
    }

    public void reload()
    {
        initDirs();
        initConfigs();
        if (bot != null)
            bot.logoutBot();
        if (loginBot())
            log.warn("Bot reloaded successfully");
        else
            log.warn("Bot failed to reload");
    }


}
