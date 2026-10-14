package me.unariginal.dexrewards.placeholders.types.dextype;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.DexTypePlaceholder;

import java.util.List;

public class DexTypeGroupCount implements DexTypePlaceholder {
    @Override
    public GenericResult handle(DexTypeConfig dexTypeConfig, List<String> args) {
        return GenericResult.valid(dexTypeConfig.rewardGroups.size());
    }

    @Override
    public List<String> id() {
        return List.of("dex_total_groups");
    }
}
