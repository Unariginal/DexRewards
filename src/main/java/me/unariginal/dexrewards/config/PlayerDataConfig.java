package me.unariginal.dexrewards.config;

import com.google.common.collect.Maps;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.data.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import static me.unariginal.dexrewards.config.ConfigManager.DEX_TYPES;
import static me.unariginal.dexrewards.utils.GsonUtils.gson;

public class PlayerDataConfig {
    public static Map<UUID, PlayerData> playerDataMap = Maps.newConcurrentMap();

    private static File getPlayerFile(UUID uuid) {
        return new File(ConfigManager.configDir, "players/" + uuid.toString() + ".json");
    }

    private static File getPlayerFile(ServerPlayerEntity player) {
        return getPlayerFile(player.getUuid());
    }

    public static void loadPlayerData(ServerPlayerEntity player) {
        File playerFile = getPlayerFile(player);
        if (!playerFile.exists()) {
            PlayerData playerData = new PlayerData();
            playerData.uuid = player.getUuidAsString();
            playerData.username = player.getNameForScoreboard();
            for (Map.Entry<String, DexTypeConfig> dexTypeEntry : DEX_TYPES.entrySet()) {
                playerData.pokedexProgress.put(dexTypeEntry.getKey(), new PlayerData.ProgressTracker());
            }
            playerDataMap.put(player.getUuid(), playerData);
            return;
        }

        PlayerData playerData = ConfigManager.loadFile("players/" + playerFile.getName(), PlayerData.class);
        if (playerData == null) {
            // TODO Maybe backup method
            return;
        }

        playerDataMap.put(player.getUuid(), playerData);
    }

    public static void savePlayerData(ServerPlayerEntity player) {
        savePlayerData(player.getUuid());
    }

    public static void savePlayerData(UUID uuid) {
        PlayerData playerData = getPlayerData(uuid);
        if (playerData == null) return;
        String json = gson.toJson(playerData);
        Path target = getPlayerFile(uuid).toPath();
        DexRewards.saveExecutor.execute(() -> {
            try {
                Files.createDirectories(target.getParent());
                Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
                Files.writeString(tmp, json);
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                DexRewards.LOGGER.error("[DexRewards] Failed to save player data for {}", uuid, e);
            }
        });
    }

    @Nullable
    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
}
