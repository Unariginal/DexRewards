package me.unariginal.dexrewards.placeholders.types.dextype;

import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.DexTypePlaceholder;

import java.util.List;

public class PokedexTotal implements DexTypePlaceholder {
    @Override
    public GenericResult handle(DexTypeConfig dexTypeConfig, List<String> args) {
        return GenericResult.valid(DexRewards.dexTypeTotals.getOrDefault(dexTypeConfig.dexTypeId, 0));
    }

    @Override
    public List<String> id() {
        return List.of("pokedex_total");
    }
}
