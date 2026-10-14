package me.unariginal.dexrewards.placeholders;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.data.rewards.RewardGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ParseContext {
    private final ServerPlayerEntity player;
    private final DexTypeConfig dexTypeConfig;
    private final RewardGroup rewardGroup;

    private ParseContext(Builder builder) {
        this.player = builder.player;
        this.dexTypeConfig = builder.dexTypeConfig;
        this.rewardGroup = builder.rewardGroup;
    }

    @Nullable
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @Nullable
    public DexTypeConfig getDexType() {
        return dexTypeConfig;
    }

    @Nullable
    public RewardGroup getRewardGroup() {
        return rewardGroup;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ServerPlayerEntity player;
        private DexTypeConfig dexTypeConfig;
        private RewardGroup rewardGroup;

        public Builder player(ServerPlayerEntity player) {
            this.player = player;
            return this;
        }

        public Builder dexType(DexTypeConfig dexTypeConfig) {
            this.dexTypeConfig = dexTypeConfig;
            return this;
        }

        public Builder rewardGroup(RewardGroup rewardGroup) {
            this.rewardGroup = rewardGroup;
            return this;
        }

        public ParseContext build() {
            return new ParseContext(this);
        }
    }
}
