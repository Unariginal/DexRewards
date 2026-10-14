package me.unariginal.dexrewards.data.rewards;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

public class ItemReward extends Reward {
    public ItemStack item;
    public int minCount;
    public int maxCount;

    @Override
    public void distributeReward(ServerPlayerEntity player) {
        if (minCount > maxCount) minCount = maxCount;
        ItemStack reward = item.copyWithCount(new Random().nextInt(minCount, maxCount + 1));
        player.getInventory().offerOrDrop(reward);
    }
}
