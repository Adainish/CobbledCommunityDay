package io.github.adainish.cobbledcommunityday.wrapper;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.CommunityPokemon;
import io.github.adainish.cobbledcommunityday.obj.DiscordAccount;
import io.github.adainish.cobbledcommunityday.storage.CommunityDayStorage;
import io.github.adainish.cobbledcommunityday.util.DiscordEmbedBuilder;
import io.github.adainish.cobbledcommunityday.util.RandomHelper;
import io.github.adainish.cobbledcommunityday.util.Util;
import org.apache.commons.lang3.time.DateUtils;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.server.Server;

import java.util.*;

public class CommunityDayWrapper
{
    private HashMap<Long, DiscordAccount> discordAccountData = new HashMap <>();
    private CommunityPokemon selectedCommunityPokemon;
    private List<CommunityPokemon> possibleCommunityPokemon = new ArrayList<>();
    private boolean votingOpen = true;
    private long voteUntil;
    private long daysUntil;
    private long waitingDays;
    private boolean hasBeenAnnounced = false;

    public CommunityDayWrapper() {}

    public List<Species> blackList() {
        List<Species> blackList = new ArrayList <>();
        try {
            for (String s:CobbledCommunityDay.config.blackListedSpecies) {
                Species species = null;
                if (PokemonSpecies.INSTANCE.getByName(s) != null)
                    species = PokemonSpecies.INSTANCE.getByName(s);
                if (species == null)
                    continue;
                blackList.add(species);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return blackList;
    }

    public void endVoting() {
        selectWinner();
        this.votingOpen = false;
    }

    public void wipeNonActiveEmotes() {
        Channel channel = CobbledCommunityDay.bot.api.getChannelById(CobbledCommunityDay.config.channelID).orElse(null);
        if (channel == null)
            return;

        try {
            if (channel.asServerChannel().isPresent()) {
                Server server = channel.asServerChannel().get().getServer();
                if (server.getCustomEmojis().isEmpty())
                    return;
                for (KnownCustomEmoji emoji : server.getCustomEmojis()) {
                    if (emoji == null)
                        continue;
                    if (!emoji.getName().contains("communityday_"))
                        continue;
                    if (!isValidEmoji(emoji.getName())) {
                        emoji.delete();
                    }
                }
            }
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }

    }

    public void selectWinnerNoAnnouncement() {
        int highestVotes = 0;
        CommunityPokemon selected = null;
        for (CommunityPokemon p:possibleCommunityPokemon) {
            if (p.getVotes() > highestVotes) {
                highestVotes = p.getVotes();
                selected = p;
            }
        }
        if (selected == null)
            selected = RandomHelper.getRandomElementFromCollection(possibleCommunityPokemon);
        selectedCommunityPokemon = selected;
    }

    public void selectWinner() {
        String channelID = CobbledCommunityDay.config.channelID;
        int highestVotes = 0;
        CommunityPokemon selected = null;
        for (CommunityPokemon p:possibleCommunityPokemon) {
            if (p.getVotes() > highestVotes) {
                highestVotes = p.getVotes();
                selected = p;
            }
        }
        if (selected == null)
            selected = RandomHelper.getRandomElementFromCollection(possibleCommunityPokemon);
        selectedCommunityPokemon = selected;
        DiscordEmbedBuilder.sendBotMessageToChannel(channelID, "Community Day Voting has closed", "The Selected winner is:");
        DiscordEmbedBuilder.sendPokemonEmbedToChannel(channelID, "Community Day Winner", "%pokemon% with %votes% votes".replace("%pokemon%", selectedCommunityPokemon.getPokemonName()).replace("%votes%", String.valueOf(selectedCommunityPokemon.getVotes())), selectedCommunityPokemon);
        DiscordEmbedBuilder.sendBotMessageToChannel(channelID, "The Community Day will start in: ", "%date%".replace("%date%", timeLeftInHoursMinutesFromString(daysUntil)));
    }

    public Pokemon randomPokemon() {
        //min int
        //max int
        int gennedInt = 5;
        Species species = RandomHelper.getRandomElementFromCollection(PokemonSpecies.INSTANCE.getImplemented());
        if (blackList().contains(species))
            return this.randomPokemon();
        return species.create(gennedInt);
    }

    public Ability getHiddenAbility(Pokemon pokemon) {
        //implement does have ha check
//        for (Ability ability:pokemon.getForm().getAbilities()) {
//            if (pokemon.getForm().getAbilities().isHiddenAbility(ability))
//                return ability;
//        }
        return pokemon.getAbility();
    }

    public boolean timesUp(long timer) {
        long currentTime = System.currentTimeMillis();
        long cd = timer - currentTime;
        long hours = cd / Util.HOUR_IN_MILLIS;
        cd = cd - (hours * Util.HOUR_IN_MILLIS);
        long minutes = cd / Util.MINUTE_IN_MILLIS;
        return hours == 0 && minutes == 0;
    }

    public String timeLeftInHoursMinutesFromString(long timer) {
        long currentTime = System.currentTimeMillis();
        long cd = timer - currentTime;
        long hours = cd / Util.HOUR_IN_MILLIS;
        cd = cd - (hours * Util.HOUR_IN_MILLIS);
        long minutes = cd / Util.MINUTE_IN_MILLIS;
        return hours + " Hours " + minutes + " Minutes";
    }


    public int getShinyRate() {
        return CobbledCommunityDay.config.shinyRate;
    }

    public int getIVSBuff() {
        return CobbledCommunityDay.config.ivsBuff;
    }

    public int getHARate() {
        return CobbledCommunityDay.config.haRate;
    }

    public double getSpawnRate() {
        return CobbledCommunityDay.config.spawnRate;
    }

    public Stats getRandomStatType() {
        List <Stats> battleStatsTypes = new ArrayList <>(Arrays.asList(Stats.values()));
        battleStatsTypes.remove(Stats.ACCURACY);
        battleStatsTypes.remove(Stats.EVASION);
        return RandomHelper.getRandomElementFromCollection(battleStatsTypes);
    }

    public double getPercentage(int decimalPlaces, Pokemon pokemon) {
        int total = 0;

        for (Stats st:Stats.values()) {
            switch (st)
            {
                case ACCURACY, EVASION -> {
                    continue;
                }
            }
            total += pokemon.getIvs().get(st);
        }

        double percentage = (double)total / 186.0 * 100.0;
        return Math.floor(percentage * Math.pow(10.0, (double)decimalPlaces)) / Math.pow(10.0, (double)decimalPlaces);
    }

    public Pokemon buffIVS(Pokemon pokemon) {
        int buffPercent = getIVSBuff();
        double pokemonPercent = getPercentage(1, pokemon);
        double leftOver = 100 - pokemonPercent;
        double increase = 0;
        while (increase < leftOver) {
            if (increase >= buffPercent)
                break;
            Stats statsType = getRandomStatType();
            if (pokemon.getIvs().get(statsType) >= 31)
                continue;
            pokemon.getIvs().set(statsType, pokemon.getIvs().get(statsType) + 1);
            increase++;
        }
        return pokemon;
    }

    public double getRandomChance() {
        return Math.floor(Math.random() * 100) + 1;
    }


    public int votingTimeMinutes() {
        return CobbledCommunityDay.config.votingTime;
    }

    public int waitingDays() {
        return CobbledCommunityDay.config.waitingTime;
    }

    public boolean isCommunityDay() {
        Date communityDay = new Date(daysUntil);
        Date currentDate = new Date(System.currentTimeMillis());
        return DateUtils.isSameDay(communityDay, currentDate);
    }

    public int daysUntil() {
        return CobbledCommunityDay.config.daysUntil;
    }


    public void createNewCommunityDay() {
        try {
            getDiscordAccountData().clear();
            selectedCommunityPokemon = null;
            possibleCommunityPokemon.clear();
            List<Pokemon> selectedMons = new ArrayList <>();
            hasBeenAnnounced = false;

            for (int i = 0; i < CobbledCommunityDay.config.maxPokemon; i++) {
                Pokemon pokemon = randomPokemon();
                if (pokemon == null)
                    continue;
                if (selectedMons.contains(pokemon))
                    continue;
                selectedMons.add(pokemon);
                CommunityPokemon communityPokemon = new CommunityPokemon(pokemon);
                possibleCommunityPokemon.add(communityPokemon);
            }
            String channelID = CobbledCommunityDay.config.channelID;
            StringBuilder mainDescription = new StringBuilder();
            this.voteUntil = System.currentTimeMillis() + (votingTimeMinutes() * Util.MINUTE_IN_MILLIS);
            this.daysUntil = System.currentTimeMillis() + (daysUntil() * Util.DAY_IN_MILLIS);
            this.waitingDays = System.currentTimeMillis() + (waitingDays() * Util.DAY_IN_MILLIS);
            TextChannel channel = null;
            if (CobbledCommunityDay.bot.api.getTextChannelById(channelID).isPresent())
                channel = CobbledCommunityDay.bot.api.getTextChannelById(channelID).get();
            if (channel == null)
                return;
            for (CommunityPokemon p:possibleCommunityPokemon) {
                if (p.getEmojiName() == null || p.getEmojiName().isEmpty())
                    continue;
                mainDescription.append(" ").append(p.getPokemonName());
            }
            DiscordEmbedBuilder.sendAnnouncementMessageToChannel(channelID, "Community Day Voting has opened, you can vote for another: %date%".replace("%date%", timeLeftInHoursMinutesFromString(voteUntil)),
                    "You can pick from the following options:" + mainDescription.toString());
            for (CommunityPokemon p:possibleCommunityPokemon) {
                if (p.getEmojiName() == null || p.getEmojiName().isEmpty())
                    continue;
                DiscordEmbedBuilder.sendPokemonEmbedToChannel(channelID, "Community Day Option", p.getPokemonName(), p);
                if (getCustomEmoji(p.getEmojiName()) != null) {
                    CustomEmoji emoji = getCustomEmoji(p.getEmojiName());
                    channel.getMessagesAsStream().findFirst().get().addReaction(emoji);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public KnownCustomEmoji getCustomEmoji(String name) {
        for (KnownCustomEmoji customEmoji:CobbledCommunityDay.bot.api.getCustomEmojis()) {
            if (customEmoji.getName().equalsIgnoreCase(name))
                return customEmoji;
        }
        return null;
    }

    public void announceCommunityDay() {
        if (selectedCommunityPokemon == null)
            return;
        String channelID = CobbledCommunityDay.config.channelID;
        DiscordEmbedBuilder.sendAnnouncementMessageToChannel(channelID, "Community Day", "@everyone The Community Day has Started! %p% will now start appearing more and experience different buffs for ivs and abilities! You might even find a few sparkling ones!".replace("%p%", selectedCommunityPokemon.getPokemonName()));
        DiscordEmbedBuilder.sendBotMessageToChannel(channelID, "Next Vote Period", "Voting for the new community day will start in %d% days!".replace("%d%", String.valueOf(daysUntil())));
        setHasBeenAnnounced(true);
    }

    public boolean shouldCreateNewCommunityDay() {
        long time = System.currentTimeMillis();
        return time > waitingDays;
    }

    public boolean shouldVotingBeOpen() {
        return !timesUp(voteUntil);
    }

    public boolean isValidEmoji(String name) {
        for (CommunityPokemon pokemon:possibleCommunityPokemon) {
            if (pokemon.getEmojiName() == null)
                return false;
            if (pokemon.getEmojiName().equals(name))
                return true;
        }
        return false;
    }

    public CommunityPokemon getCommunityPokemonFromEmoji(String id) {
        for (CommunityPokemon pokemon:possibleCommunityPokemon) {
            if (pokemon.getEmojiName().equals(id))
                return pokemon;
        }
        return null;
    }
    public CommunityPokemon getSelectedCommunityPokemon() {
        return selectedCommunityPokemon;
    }

    public void setSelectedCommunityPokemon(CommunityPokemon selectedCommunityPokemon) {
        this.selectedCommunityPokemon = selectedCommunityPokemon;
    }

    public long getDaysUntil() {
        return daysUntil;
    }

    public void setDaysUntil(long daysUntil) {
        this.daysUntil = daysUntil;
    }

    public HashMap <Long, DiscordAccount> getDiscordAccountData() {
        return discordAccountData;
    }

    public void setDiscordAccountData(HashMap <Long, DiscordAccount> discordAccountData) {
        this.discordAccountData = discordAccountData;
    }

    public void save() {
        CommunityDayStorage.saveCommunityDay(this);
    }

    public boolean isVotingOpen() {
        return selectedCommunityPokemon == null;
    }

    public void setVotingOpen(boolean votingOpen) {
        this.votingOpen = votingOpen;
    }

    public long getVoteUntil() {
        return voteUntil;
    }

    public void setVoteUntil(long voteUntil) {
        this.voteUntil = voteUntil;
    }

    public long getWaitingDays() {
        return waitingDays;
    }

    public void setWaitingDays(long waitingDays) {
        this.waitingDays = waitingDays;
    }

    public boolean hasBeenAnnounced() {
        return hasBeenAnnounced;
    }

    public void setHasBeenAnnounced(boolean hasBeenAnnounced) {
        this.hasBeenAnnounced = hasBeenAnnounced;
    }
}
