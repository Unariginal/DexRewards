package me.unariginal.dexrewards.placeholders.types.player;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.PlaceholderManager;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerDexTypePlaceholder;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Comparator;
import java.util.List;

public class PlayerRewardGroup implements PlayerDexTypePlaceholder {
    @Override
    public GenericResult handle(ServerPlayerEntity player, DexTypeConfig dexTypeConfig, List<String> args) {
        PlayerData.ProgressTracker tracker = PlaceholderManager.getProgress(player, dexTypeConfig);
        if (tracker == null) return GenericResult.invalid("NO PROGRESS");

        return GenericResult.valid(dexTypeConfig.rewardGroups.values().stream()
                .filter(group -> tracker.claimedRewards.contains(group.rewardGroupId))
                .max(Comparator.comparingDouble(group -> group.requiredPercent))
                .map(group -> group.displayName)
                .orElse("None"));
    }

    @Override
    public List<String> id() {
        return List.of("player_reward_group");
    }
}
