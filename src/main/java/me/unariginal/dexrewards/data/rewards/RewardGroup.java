package me.unariginal.dexrewards.data.rewards;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardGroup {
    public transient String rewardGroupId;
    public double requiredPercent;
    public String displayName;
    public ItemStack iconItem = Items.DIRT.getDefaultStack();
    public List<String> iconLore = new ArrayList<>();
    public Map<String, Reward> rewards = new HashMap<>();

    public void distributeRewards(ServerPlayerEntity player) {
        for (Reward reward : rewards.values()) {
            reward.distributeReward(player);
        }
    }
}
