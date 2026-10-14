package me.unariginal.dexrewards.data.rewards;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;

public class RewardGroup {
    public transient String rewardGroupId;
    public double requiredPercent;
    public String displayName;
    public ItemStack iconItem;
    public List<String> iconLore;
    public Map<String, Reward> rewards;

    public void distributeRewards(ServerPlayerEntity player) {
        for (Reward reward : rewards.values()) {
            reward.distributeReward(player);
        }
    }
}
