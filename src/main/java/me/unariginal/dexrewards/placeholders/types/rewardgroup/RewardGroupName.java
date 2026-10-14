package me.unariginal.dexrewards.placeholders.types.rewardgroup;

import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.RewardGroupPlaceholder;

import java.util.List;

public class RewardGroupName implements RewardGroupPlaceholder {
    @Override
    public GenericResult handle(RewardGroup rewardGroup, List<String> args) {
        return GenericResult.valid(rewardGroup.displayName);
    }

    @Override
    public List<String> id() {
        return List.of("reward_group_name", "group_name");
    }
}
