package me.unariginal.dexrewards.data;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.handlers.PokedexProgressHandler;
import me.unariginal.dexrewards.placeholders.ParseContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

import static me.unariginal.dexrewards.config.ConfigManager.DEX_TYPES;
import static me.unariginal.dexrewards.config.ConfigManager.MESSAGES;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;

public class PlayerData {
    public String uuid;
    public String username;
    public Map<String, ProgressTracker> pokedexProgress = new HashMap<>();
    public Map<String, Set<String>> caughtShinies = new HashMap<>();

    public static class ProgressTracker {
        public int progressCount = 0;
        public List<String> claimedRewards = new ArrayList<>();
        public List<String> claimableRewards = new ArrayList<>();
    }

    public ProgressTracker getProgress(String dexTypeId) {
        for (Map.Entry<String, ProgressTracker> tracker : pokedexProgress.entrySet()) {
            if (tracker.getKey().equals(dexTypeId)) {
                return tracker.getValue();
            }
        }
        return null;
    }

    public boolean addCaughtShiny(Identifier speciesId, String formName) {
        return caughtShinies.computeIfAbsent(speciesId.toString(), id -> new HashSet<>())
                .add(formName.toLowerCase(Locale.ROOT));
    }

    public boolean hasCaughtShiny(Identifier speciesId) {
        return caughtShinies.containsKey(speciesId.toString());
    }

    public boolean hasCaughtShiny(Identifier speciesId, String formName) {
        Set<String> forms = caughtShinies.get(speciesId.toString());
        return forms != null && forms.contains(formName.toLowerCase(Locale.ROOT));
    }

    public boolean updateProgressCount() {
        PokedexManager manager = Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(UUID.fromString(uuid));
        boolean changed = false;

        for (DexTypeConfig dexTypeConfig : DEX_TYPES.values()) {
            ProgressTracker tracker = pokedexProgress.computeIfAbsent(dexTypeConfig.dexTypeId, id -> new ProgressTracker());
            int count = PokedexProgressHandler.countProgress(dexTypeConfig, manager, this);
            if (tracker.progressCount != count) {
                tracker.progressCount = count;
                changed = true;
            }
        }
        return changed;
    }

    public void updateClaimableRewards() {
        ServerPlayerEntity player = DexRewards.INSTANCE.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
        if (player == null) return;

        for (DexTypeConfig dexTypeConfig : DEX_TYPES.values()) {
            ProgressTracker tracker = pokedexProgress.computeIfAbsent(dexTypeConfig.dexTypeId, id -> new ProgressTracker());
            int oldSize = tracker.claimableRewards.size();
            List<String> newClaimableRewards = new ArrayList<>();
            dexTypeConfig.rewardGroups.forEach((groupId, group) -> {
                if (!tracker.claimedRewards.contains(group.rewardGroupId)) {
                    double percent = ((double) tracker.progressCount / DexRewards.dexTypeTotals.get(dexTypeConfig.dexTypeId)) * 100.0;
                    if (percent >= group.requiredPercent) {
                        newClaimableRewards.add(group.rewardGroupId);
                        if (!tracker.claimableRewards.contains(group.rewardGroupId)) {
                            player.sendMessage(deserialize(MESSAGES.rewardClaimable, ParseContext.builder().player(player).dexType(dexTypeConfig).rewardGroup(group).build()));
                        }
                    }
                }
            });

            tracker.claimableRewards = newClaimableRewards;
            if (oldSize < tracker.claimableRewards.size()) {
                player.sendMessage(deserialize(MESSAGES.rewardsToClaim, ParseContext.builder().player(player).dexType(dexTypeConfig).build()));
            }
        }
    }
}
