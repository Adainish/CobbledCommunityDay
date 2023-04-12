package io.github.adainish.cobbledcommunityday.obj;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.Util;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import org.apache.logging.log4j.Level;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

public class Bot
{
    private String serverID;

    public Bot() {}

    public DiscordApi api;

    public void logoutBot() {
        if (api == null)
            return;
        api.getReactionAddListeners().forEach(l -> {
            api.removeListener(ReactionAddListener.class, l);
        });
        api.disconnect();
    }

    public void loginBot(String args) throws Exception {
        if (args.isEmpty())
            return;
        try {
            this.api = new DiscordApiBuilder()
                    .setToken(args)
                    .setAllIntents()
                    .login()
                    .join();
        } catch (Exception e) {
            CobbledCommunityDay.getLog().error(e.getMessage());
            throw new Exception("Failed to log in");
        }
        if (this.api != null) {
            this.api.updateActivity(ActivityType.COMPETING, "Sorting out the Community Day!");
            CobbledCommunityDay.getLog().log(Level.WARN,"Community Day Bot has launched successfully!");
            // Print the invite url of your bot
            CobbledCommunityDay.getLog().log(Level.WARN,"You can invite the bot by using the following url: " + generateInvite(api));
            initVotingListener(this.api);
        }
    }

    public void initVotingListener(DiscordApi api) {
        api.addReactionAddListener(reactionAddEvent ->  {
            if (reactionAddEvent.getEmoji().isCustomEmoji()) {
                if (reactionAddEvent.getUser().get().isBot())
                    return;
                CommunityDayWrapper communityDay = CobbledCommunityDay.wrapper;
                if (!communityDay.isVotingOpen())
                    return;
                CustomEmoji emoji = reactionAddEvent.getEmoji().asCustomEmoji().get();
                if (emoji.getName() == null) {
                    CobbledCommunityDay.getLog().error("Something went horribly wrong! The Emoji Data for a Community Day Pokemon was detected to be null, or non existent! This indicates a storage issue! Please contact the dev and check the storage file to verify!");
                    return;
                }
                if (communityDay.isValidEmoji(emoji.getName())) {
                    DiscordAccount discordAccount = Util.getDiscordAccount(reactionAddEvent.getUserId());
                    if (discordAccount == null)
                        discordAccount = new DiscordAccount(reactionAddEvent.getUserId());
                    CommunityPokemon pokemon = communityDay.getCommunityPokemonFromEmoji(emoji.getName());
                    if (discordAccount.hasVoted()) {
                        reactionAddEvent.getUser().get().sendMessage("You've already voted for this community day, your vote was cast for: " + discordAccount.getSelectedPokemon());
                        return;
                    }
                    discordAccount.setVoted(true);
                    discordAccount.setSelectedPokemon(pokemon.getPokemonName());
                    communityDay.getDiscordAccountData().put(discordAccount.getDiscordUserID(), discordAccount);
                    communityDay.getCommunityPokemonFromEmoji(emoji.getName()).increaseVotes(1);
                    communityDay.save();
                    reactionAddEvent.getUser().get().sendMessage("You've voted for: " + pokemon.getPokemonName());
                }
            }
        });
    }



    public String generateInvite(DiscordApi api) {
        return api.createBotInvite();
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
}
