package me.unariginal.dexrewards.data.rewards;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class Reward {
    public transient String rewardId;
    public final transient UUID uuid = UUID.randomUUID();
    public String displayName;

    public void distributeReward(ServerPlayerEntity player) {
    }
}
