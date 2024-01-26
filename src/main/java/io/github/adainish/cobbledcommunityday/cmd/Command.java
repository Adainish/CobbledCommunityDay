package io.github.adainish.cobbledcommunityday.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.obj.CommunityPokemon;
import io.github.adainish.cobbledcommunityday.util.Util;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Command
{
    public static String formattedString(String s) {
        return s.replaceAll("&([0-9a-fk-or])", "\u00a7$1");
    }
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("cobbledcommunityday")
                .executes(context -> {
                    List<String> communityDayCurrentInfo = new ArrayList<>();
                    communityDayCurrentInfo.add("&7&m----------------------------------------");
                    communityDayCurrentInfo.add("&6&lCobbled Community Day");
                    communityDayCurrentInfo.add("&7&m----------------------------------------");
                    if (CobbledCommunityDay.wrapper != null) {
                        communityDayCurrentInfo.add("&eCurrent Community Day: &f" + (CobbledCommunityDay.wrapper.isCommunityDay() ? CobbledCommunityDay.wrapper.getSelectedCommunityPokemon().getPokemonName() : "Community Day hasn't started yet!"));
                        // check if voting is open
                        if (CobbledCommunityDay.wrapper.isVotingOpen()) {
                            communityDayCurrentInfo.add("&eVoting is open! &fvote for your favourite pokemon on discord!");
                            // grab which voting options are available, and what the current votes are
                            if (CobbledCommunityDay.wrapper.getSelectedCommunityPokemon() != null) {
                                communityDayCurrentInfo.add("&eVoting Options:");
                                for (CommunityPokemon pokemon : CobbledCommunityDay.wrapper.getPossibleCommunityPokemon()) {
                                    communityDayCurrentInfo.add("&f" + pokemon.getPokemonName() + " &e- &f" + pokemon.getVotes() + " votes");
                                }
                            }
                        } else {
                            communityDayCurrentInfo.add("&cVoting for the community day is closed!");
                            // get voting winner
                            if (CobbledCommunityDay.wrapper.getSelectedCommunityPokemon() != null) {
                                communityDayCurrentInfo.add("&eThe winner is: &f" + CobbledCommunityDay.wrapper.getSelectedCommunityPokemon().getPokemonName());
                            }
                        }
                        if (CobbledCommunityDay.wrapper.isCommunityDay()) {
                            // send info about time left until midnight
                            // calculate time until midnight from current time
                            long timeUntilMidnight = Util.DAY_IN_MILLIS - (System.currentTimeMillis() % Util.DAY_IN_MILLIS);
                            communityDayCurrentInfo.add("&eCommunity Day ends in: &f" + Util.getTimeString(timeUntilMidnight));
                        } else {
                            communityDayCurrentInfo.add("&eCommunity Day starts in: &f" + Util.getTimeString(CobbledCommunityDay.wrapper.getDaysUntil() - System.currentTimeMillis()));
                        }

                        // send info about shiny rate
                        communityDayCurrentInfo.add("&eShiny Rate: &f" + CobbledCommunityDay.wrapper.getShinyRate() + "%");
                        // send info about HA rate
                        communityDayCurrentInfo.add("&eHA Rate: &f" + CobbledCommunityDay.wrapper.getHARate() + "%");
                        // send info about IVs buff
                        communityDayCurrentInfo.add("&eIVs Buff: &f" + CobbledCommunityDay.wrapper.getIVSBuff() + "%");
                        // send info about spawn rate
                        communityDayCurrentInfo.add("&eSpawn Rate: &f" + CobbledCommunityDay.wrapper.getSpawnRate() + "%");
                    } else {
                        communityDayCurrentInfo.add("&cCommunity day isn't active!");
                    }

                    communityDayCurrentInfo.add("&7&m----------------------------------------");

                    communityDayCurrentInfo.forEach(s -> context.getSource().sendSystemMessage(Component.literal(formattedString(s))));
                    return 1;
                })
                .then(Commands.literal("help")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            List<String> infoList = new ArrayList<>();
                            infoList.add("&7&m----------------------------------------");
                            infoList.add("&6&lCobbled Community Day");
                            infoList.add("&7&m----------------------------------------");
                            infoList.add("&e/cobbledcommunityday help &7- &fShows this help menu.");
                            infoList.add("&e/cobbledcommunityday info &7- &fShows information about the plugin.");
                            infoList.add("&e/cobbledcommunityday reload &7- &fReloads the plugin.");
                            infoList.add("&e/cobbledcommunityday version &7- &fShows the plugin version.");
                            infoList.add("&e/cobbledcommunityday discord &7- &fShows the plugin discord.");
                            infoList.add("&e/cobbledcommunityday test &7- &fTest command.");
                            infoList.add("&e/cobbledcommunityday create &7- &fCreates a new Community Day.");
                            infoList.add("&e/cobbledcommunityday force &7- &fForces a Community Day.");
                            infoList.add("&e/cobbledcommunityday closevote &7- &fCloses voting.");
                            infoList.add("&7&m----------------------------------------");
                            infoList.forEach(s -> context.getSource().sendSystemMessage(Component.literal(formattedString(s))));
                    return 1;
                }
                ))
                .then(Commands.literal("info")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            List<String> infoList = new ArrayList<>();
                            infoList.add("&7&m----------------------------------------");
                            infoList.add("&6&lCobbled Community Day");
                            infoList.add("&7&m----------------------------------------");
                            infoList.add("&eAuthors:" + CobbledCommunityDay.AUTHORS);
                            infoList.add("&eVersion:" + CobbledCommunityDay.VERSION);
                            if (CobbledCommunityDay.bot.jda != null)
                                infoList.add("&eDiscord: &f" + CobbledCommunityDay.bot.generateInvite(CobbledCommunityDay.bot.jda));
                            else infoList.add("&eDiscord: &fBot not logged in or not configured.");
                            infoList.add("&7&m----------------------------------------");
                            infoList.forEach(s -> context.getSource().sendSystemMessage(Component.literal(formattedString(s))));
                            return 1;
                        }
                        ))
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            CobbledCommunityDay.instance.reload();
                            context.getSource().sendSystemMessage(Component.literal(formattedString("&aReloaded!")));
                            return 1;
                        }
                        ))
                .then(Commands.literal("version")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.literal(formattedString("&aVersion: " + CobbledCommunityDay.VERSION)));
                            return 1;
                        }
                        ))
                .then(Commands.literal("discord")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            if (CobbledCommunityDay.bot.jda != null)
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&aDiscord: " + CobbledCommunityDay.bot.generateInvite(CobbledCommunityDay.bot.jda))));
                            else context.getSource().sendSystemMessage(Component.literal(formattedString("&aDiscord: &fBot not logged in or not configured.")));
                            return 1;
                        }
                        ))
                .then(Commands.literal("test")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.literal(formattedString("&aTest!")));
                            return 1;
                        }
                        ))

                .then(Commands.literal("create")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                            if (wrapper != null) {
                                wrapper.createNewCommunityDay();
                                wrapper.save();
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&aCreated new Community Day!")));
                            } else {
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&cwrapper null, making a new one! Please reuse the command!")));
                                CobbledCommunityDay.wrapper = new CommunityDayWrapper();
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("force")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                            if (wrapper != null) {
                                wrapper.setDaysUntil(System.currentTimeMillis());
                                wrapper.save();
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&aForced Community Day!")));
                            }  else {
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&cNo Community Day found!")));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("closevote")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                            if (wrapper != null) {
                                wrapper.endVoting();
                                wrapper.save();
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&aClosed voting!")));
                            } else {
                                context.getSource().sendSystemMessage(Component.literal(formattedString("&cNo Community Day found!")));
                            }
                            return 1;
                        })
                )
                ;
    }
}
