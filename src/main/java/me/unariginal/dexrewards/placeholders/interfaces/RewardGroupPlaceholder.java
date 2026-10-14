package me.unariginal.dexrewards.placeholders.interfaces;

import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.placeholders.GenericResult;

import java.util.List;

public interface RewardGroupPlaceholder {
    GenericResult handle(RewardGroup rewardGroup, List<String> args);
    List<String> id();
}
