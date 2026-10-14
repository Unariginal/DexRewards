package me.unariginal.dexrewards.placeholders.types.rewardgroup;

import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.RewardGroupPlaceholder;
import me.unariginal.dexrewards.utils.TextUtils;

import java.util.List;

public class RewardGroupPercent implements RewardGroupPlaceholder {
    @Override
    public GenericResult handle(RewardGroup rewardGroup, List<String> args) {
        return GenericResult.valid(TextUtils.formatPercent(rewardGroup.requiredPercent));
    }

    @Override
    public List<String> id() {
        return List.of("reward_group_percent", "group_percent");
    }
}
