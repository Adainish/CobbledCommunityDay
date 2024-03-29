package io.github.adainish.cobbledcommunityday.subscriptions;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;
import kotlin.Unit;

import java.util.stream.IntStream;

public class EventSubscriptions
{
    public EventSubscriptions()
    {
        subsScribeToSpawnEvent();
    }

    public void subsScribeToSpawnEvent()
    {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {
            try {
                if (!CobbledCommunityDay.wrapper.isCommunityDay()) {
                    return Unit.INSTANCE;
                }
                    CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                    PokemonEntity entity = event.getEntity();
                    Species species = entity.getPokemon().getSpecies();
                    if (wrapper.getSelectedCommunityPokemon() == null) {
                        wrapper.selectWinnerNoAnnouncement();
                        wrapper.save();
                        return Unit.INSTANCE;
                    }

                    if (wrapper.getSelectedCommunityPokemon().getPokemon().getSpecies().equals(species)) {
                        Pokemon newPokemon = wrapper.getSelectedCommunityPokemon().getPokemon();
                        if (wrapper.getRandomChance() <= wrapper.getShinyRate()) {
                            entity.getPokemon().setShiny(true);
                        }
                        if (wrapper.getRandomChance() <= wrapper.getHARate()) {
                            //Set HA once implemented by Cobblemon
                        }

                        int buffPercent = wrapper.getIVSBuff();
                        double pokemonPercent = CobbledCommunityDay.wrapper.getPercentage(1, newPokemon);
                        double leftOver = 100 - pokemonPercent;
                        IntStream.iterate(0, i -> i < leftOver, i -> i + 1).takeWhile(i -> i < buffPercent).mapToObj(i -> wrapper.getRandomStatType()).filter(statsType -> entity.getPokemon().getIvs().getOrDefault(statsType) < 31).forEachOrdered(statsType -> entity.getPokemon().getIvs().set(statsType, entity.getPokemon().getIvs().getOrDefault(statsType) + 1));
                    }
                    else {
                        PokemonProperties properties = wrapper.getSelectedCommunityPokemon().getPokemonProperties();
                        if (wrapper.getRandomChance() <= wrapper.getSpawnRate()) {
                            if (wrapper.getRandomChance() <= wrapper.getShinyRate()) {
                                properties.setShiny(true);
                            }
                            if (wrapper.getRandomChance() <= wrapper.getHARate()) {
                                //Set HA once implemented by Cobblemon
                            }
                        }

                        PokemonEntity pokemonEntity = properties.createEntity(entity.level());
                        entity.level().addFreshEntity(pokemonEntity);
                        pokemonEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                    }
            } catch (Exception e)
            {
                return Unit.INSTANCE;
            }

            return Unit.INSTANCE;
        });

    }
}
