package io.github.adainish.cobbledcommunityday.subscriptions;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import kotlin.Unit;

public class EventSubscriptions
{
    public EventSubscriptions()
    {
        subsScribeToSpawnEvent();
    }

    public void subsScribeToSpawnEvent()
    {
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, event -> {
            try {
                if (!CobbledCommunityDay.wrapper.isCommunityDay()) {
                    return Unit.INSTANCE;
                }
                    CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                    PokemonEntity entity = event.getPokemon().getEntity();
                    Species species = entity.getPokemon().getSpecies();
                    if (wrapper.getSelectedCommunityPokemon() == null) {
                        wrapper.selectWinnerNoAnnouncement();
                        wrapper.save();
                        return Unit.INSTANCE;
                    }
                    Pokemon newPokemon = wrapper.getSelectedCommunityPokemon().getPokemon();
                    if (wrapper.getSelectedCommunityPokemon().getPokemon().getSpecies().equals(species)) {
                        if (wrapper.getRandomChance() <= wrapper.getShinyRate()) {
                            entity.getPokemon().setShiny(true);
                        }
                        if (wrapper.getRandomChance() <= wrapper.getHARate()) {
//                            if (!entity.getPokemon().getForm().getAbilities().isHiddenAbility(entity.getPokemon().getAbility()))
//                                entity.getPokemon().setAbility(wrapper.getHiddenAbility(entity.getPokemon()));
                        }
                        int buffPercent = wrapper.getIVSBuff();
                        double pokemonPercent = CobbledCommunityDay.wrapper.getPercentage(1, newPokemon);
                        double leftOver = 100 - pokemonPercent;
                        for (int i = 0; i < leftOver; i++) {
                            if (i >= buffPercent)
                                break;
                            Stats statsType = wrapper.getRandomStatType();
                            if (entity.getPokemon().getIvs().get(statsType) >= 31)
                                continue;
                            entity.getPokemon().getIvs().set(statsType, entity.getPokemon().getIvs().get(statsType) + 1);
                        }
                    }
                    else {
                        if (wrapper.getRandomChance() <= wrapper.getSpawnRate()) {
                            if (wrapper.getRandomChance() <= wrapper.getShinyRate()) {
                                newPokemon.setShiny(true);
                            }
                            if (wrapper.getRandomChance() <= wrapper.getHARate()) {
//                                if (!newPokemon.getForm().getAbilities().isHiddenAbility(newPokemon.getAbility()))
//                                    newPokemon.setAbility(wrapper.getHiddenAbility(newPokemon));
                            }
//                            Entity cause = event.action.spawnLocation.cause;
//                            PlayerEntity playerEntity = entity.world.getClosestPlayer(cause.getPosition().getX(), cause.getPosition().getY(), cause.getPosition().getZ(), 100, true);
//                            if (playerEntity == null) {
//                                return;
//                            }
//                            PixelmonEntity pixelmonEntity = newPokemon.getOrCreatePixelmon(playerEntity.world, playerEntity.getPosX() + RandomHelper.getRandomNumberBetween(6, 20), playerEntity.getPosY() , playerEntity.getPosZ() + RandomHelper.getRandomNumberBetween(6, 20));
//                            newPokemon.getOrSpawnPixelmon(pixelmonEntity);
                        }
                    }
            } catch (Exception e)
            {
                return Unit.INSTANCE;
            }

            return Unit.INSTANCE;
        });

    }
}
