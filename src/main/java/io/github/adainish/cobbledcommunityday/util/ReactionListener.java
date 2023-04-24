package io.github.adainish.cobbledcommunityday.util;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.CommunityPokemon;
import io.github.adainish.cobbledcommunityday.obj.DiscordAccount;
import io.github.adainish.cobbledcommunityday.subscriptions.EventSubscriptions;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter
{

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {

        CobbledCommunityDay.getLog().warn("Bot is ready!");
//        CobbledCommunityDay.bot.api = event.getJDA();
        CobbledCommunityDay.instance.setOrCreateCommunityDay();
        //register spawn listener
        CobbledCommunityDay.subscriptions = new EventSubscriptions();
        CobbledCommunityDay.instance.startTasks();
        CobbledCommunityDay.getLog().log(Level.WARN,"Community Day Bot has launched successfully!");
        // Print the invite url of your bot
        CobbledCommunityDay.getLog().log(Level.WARN,"You can invite the bot by using the following url: " + CobbledCommunityDay.bot.generateInvite(event.getJDA()));

    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        try {
            if (event.getUser().isBot() || event.getUser().isSystem())
                return;

            CommunityDayWrapper communityDay = CobbledCommunityDay.wrapper;
            if (!communityDay.isVotingOpen())
                return;

            CustomEmoji emoji = event.getReaction().getEmoji().asCustom();

            if (emoji.getName() == null) {
                CobbledCommunityDay.getLog().error("Something went horribly wrong! The Emoji Data for a Community Day Pokemon was detected to be null, or non existent! This indicates a storage issue! Please contact the dev and check the storage file to verify!");
                return;
            }

            if (communityDay.isValidEmoji(emoji.getName())) {
                DiscordAccount discordAccount = Util.getDiscordAccount(event.getUserIdLong());
                if (discordAccount == null)
                    discordAccount = new DiscordAccount(event.getUserIdLong());
                CommunityPokemon pokemon = communityDay.getCommunityPokemonFromEmoji(emoji.getName());
                if (discordAccount.hasVoted()) {
                    DiscordAccount finalDiscordAccount = discordAccount;
                    event.getUser().openPrivateChannel().queue(privateChannel -> {
                       privateChannel.sendMessage("You've already voted for this community day, your vote was cast for: " + finalDiscordAccount.getSelectedPokemon());
                    });
                    return;
                }
                discordAccount.setVoted(true);
                discordAccount.setSelectedPokemon(pokemon.getPokemonName());
                communityDay.getDiscordAccountData().put(discordAccount.getDiscordUserID(), discordAccount);
                communityDay.getCommunityPokemonFromEmoji(emoji.getName()).increaseVotes(1);
                communityDay.save();
                event.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You've voted for: " + pokemon.getPokemonName());
                });
            }

        } catch (Exception e)
        {

        }

    }

}
