package io.github.adainish.cobbledcommunityday;

import io.github.adainish.cobbledcommunityday.config.Config;
import io.github.adainish.cobbledcommunityday.obj.Bot;
import io.github.adainish.cobbledcommunityday.scheduler.AsyncTask;
import io.github.adainish.cobbledcommunityday.storage.CommunityDayStorage;
import io.github.adainish.cobbledcommunityday.subscriptions.EventSubscriptions;
import io.github.adainish.cobbledcommunityday.task.AnnouncementCheck;
import io.github.adainish.cobbledcommunityday.task.BroadcastTask;
import io.github.adainish.cobbledcommunityday.task.CreationCheck;
import io.github.adainish.cobbledcommunityday.task.VotingCheck;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CobbledCommunityDay.MODID)
public class CobbledCommunityDay {

    public static CobbledCommunityDay instance;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobbledcommunityday";
    public static final String MOD_NAME = "CommunityDay";
    public static final String VERSION = "1.0.0-Beta";
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
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
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


    private void commonSetup(final FMLCommonSetupEvent event) {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        initDirs();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        server = ServerLifecycleHooks.getCurrentServer();
        initConfigs();
        if (loginBot()) {
            log.warn("Bot creation appeared successfull, awaiting JDA initialisation");
        } else {
            log.warn("Could not log into the community bot, community day will not be active");
        }
    }

    public void startTasks()
    {
        log.warn("Starting tasks for Community Day");
        AsyncTask.Builder builder = new AsyncTask.Builder();
        AsyncTask announcementRunnableTask = builder.withInfiniteIterations().withInterval(20)
                .withRunnable(new AnnouncementCheck())
                .build();
        announcementRunnableTask.start();
        AsyncTask broadCastTask = builder.withInfiniteIterations().withInterval( (20 * 60 ) * 30)
                .withRunnable(new BroadcastTask())
                .build();
        broadCastTask.start();
        AsyncTask creationRunnableTask = builder.withInfiniteIterations().withInterval(20)
                .withRunnable(new CreationCheck())
                .build();
        creationRunnableTask.start();
        AsyncTask votingRunnableTask = builder.withInfiniteIterations().withInterval(20)
                .withRunnable(new VotingCheck())
                .build();
        votingRunnableTask.start();
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
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/CommunityDay/"));
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
}
