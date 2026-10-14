package me.unariginal.dexrewards.placeholders.types;

import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.ServerPlaceholder;

import java.util.List;

import static me.unariginal.dexrewards.config.ConfigManager.MESSAGES;

public class DexRewardsPrefix implements ServerPlaceholder {
    @Override
    public GenericResult handle(List<String> args) {
        return GenericResult.valid(MESSAGES.prefix);
    }

    @Override
    public List<String> id() {
        return List.of("dexrewards_prefix", "prefix");
    }
}
