package me.unariginal.dexrewards.placeholders.types.player;

import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.PlaceholderManager;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerDexTypePlaceholder;
import me.unariginal.dexrewards.utils.TextUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class PlayerDexPercent implements PlayerDexTypePlaceholder {
    @Override
    public GenericResult handle(ServerPlayerEntity player, DexTypeConfig dexTypeConfig, List<String> args) {
        PlayerData.ProgressTracker tracker = PlaceholderManager.getProgress(player, dexTypeConfig);
        if (tracker == null) return GenericResult.invalid("NO PROGRESS");

        int total = DexRewards.dexTypeTotals.getOrDefault(dexTypeConfig.dexTypeId, 0);
        double percent = total > 0 ? (double) tracker.progressCount / total * 100.0 : 0.0;
        return GenericResult.valid(TextUtils.formatPercent(percent));
    }

    @Override
    public List<String> id() {
        return List.of("player_dex_percent");
    }
}
