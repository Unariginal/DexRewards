package me.unariginal.dexrewards.data.rewards;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import me.unariginal.dexrewards.DexRewards;
import net.minecraft.server.network.ServerPlayerEntity;

public class PokemonReward extends Reward {
    public PokemonProperties pokemon;

    @Override
    public void distributeReward(ServerPlayerEntity player) {
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        if (pokemon != null) party.add(pokemon.create(player));
        else DexRewards.LOGGER.error("[DexRewards] Pokemon was null!");
    }
}
