package me.unariginal.dexrewards.handlers;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonAspectsChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.dexrewards.config.PlayerDataConfig;
import me.unariginal.dexrewards.data.PlayerData;

import java.util.UUID;

public class EventHandler {
    public static void registerEvents() {
        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(EventHandler::pokedexDataChanged);
        CobblemonEvents.POKEMON_GAINED.subscribe(EventHandler::pokemonGained);
        CobblemonEvents.POKEMON_ASPECTS_CHANGED.subscribe(EventHandler::pokemonAspectsChanged);
    }

    private static void pokedexDataChanged(PokedexDataChangedEvent.Post event) {
        PlayerData playerData = PlayerDataConfig.getPlayerData(event.getPlayerUUID());

        if (playerData != null && playerData.updateProgressCount()) {
            playerData.updateClaimableRewards();
            PlayerDataConfig.savePlayerData(event.getPlayerUUID());
        }
    }

    private static void pokemonGained(PokemonGainedEvent event) {
        recordShiny(event.getPlayerId(), event.getPokemon());
    }

    private static void pokemonAspectsChanged(PokemonAspectsChangedEvent event) {
        if (event.getOwnerId() != null) recordShiny(event.getOwnerId(), event.getPokemon());
    }

    private static void recordShiny(UUID playerId, Pokemon pokemon) {
        PlayerData playerData = PlayerDataConfig.getPlayerData(playerId);
        if (playerData != null && PokedexProgressHandler.recordCaughtShiny(playerData, pokemon)) {
            playerData.updateProgressCount();
            playerData.updateClaimableRewards();
            PlayerDataConfig.savePlayerData(playerId);
        }
    }
}
