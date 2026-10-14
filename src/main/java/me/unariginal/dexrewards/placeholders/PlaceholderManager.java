package me.unariginal.dexrewards.placeholders;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.config.PlayerDataConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.placeholders.interfaces.DexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerDexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerPlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.RewardGroupPlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.ServerPlaceholder;
import me.unariginal.dexrewards.placeholders.types.DexRewardsPrefix;
import me.unariginal.dexrewards.placeholders.types.dextype.DexTypeGroupCount;
import me.unariginal.dexrewards.placeholders.types.dextype.DexTypeName;
import me.unariginal.dexrewards.placeholders.types.dextype.PokedexTotal;
import me.unariginal.dexrewards.placeholders.types.player.PlayerDexCount;
import me.unariginal.dexrewards.placeholders.types.player.PlayerDexPercent;
import me.unariginal.dexrewards.placeholders.types.player.PlayerName;
import me.unariginal.dexrewards.placeholders.types.player.PlayerRewardGroup;
import me.unariginal.dexrewards.placeholders.types.rewardgroup.RewardGroupName;
import me.unariginal.dexrewards.placeholders.types.rewardgroup.RewardGroupPercent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.unariginal.dexrewards.config.ConfigManager.CONFIG;
import static me.unariginal.dexrewards.config.ConfigManager.DEX_TYPES;

public class PlaceholderManager {
    public static boolean usingPlaceholderAPI;
    public static PlaceholderAPIService placeholderAPIService;

    public static final List<ServerPlaceholder> serverPlaceholders = List.of(
            new DexRewardsPrefix()
    );

    public static final List<PlayerPlaceholder> playerPlaceholders = List.of(
            new PlayerName()
    );

    public static final List<DexTypePlaceholder> dexTypePlaceholders = List.of(
            new DexTypeName(),
            new PokedexTotal(),
            new DexTypeGroupCount()
    );

    public static final List<PlayerDexTypePlaceholder> playerDexTypePlaceholders = List.of(
            new PlayerDexCount(),
            new PlayerDexPercent(),
            new PlayerRewardGroup()
    );

    public static final List<RewardGroupPlaceholder> rewardGroupPlaceholders = List.of(
            new RewardGroupName(),
            new RewardGroupPercent()
    );

    public static void registerPlaceholders() {
        usingPlaceholderAPI = FabricLoader.getInstance().isModLoaded("placeholder-api");
        if (!usingPlaceholderAPI) return;
        if (placeholderAPIService == null) placeholderAPIService = new PlaceholderAPIService();

        serverPlaceholders.forEach(placeholderAPIService::registerServer);
        playerPlaceholders.forEach(placeholderAPIService::registerPlayer);
        dexTypePlaceholders.forEach(placeholderAPIService::registerDexType);
        playerDexTypePlaceholders.forEach(placeholderAPIService::registerPlayerDexType);
    }

    @Nullable
    public static DexTypeConfig resolveDexType(@Nullable DexTypeConfig contextDexType, List<String> args) {
        if (!args.isEmpty() && DEX_TYPES.containsKey(args.getFirst())) return DEX_TYPES.get(args.getFirst());
        if (contextDexType != null) return contextDexType;
        return CONFIG.defaultDexType != null ? DEX_TYPES.get(CONFIG.defaultDexType) : null;
    }

    @Nullable
    public static PlayerData.ProgressTracker getProgress(ServerPlayerEntity player, DexTypeConfig dexTypeConfig) {
        PlayerData playerData = PlayerDataConfig.getPlayerData(player.getUuid());
        return playerData != null ? playerData.pokedexProgress.get(dexTypeConfig.dexTypeId) : null;
    }
}
