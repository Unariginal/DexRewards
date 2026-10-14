package me.unariginal.dexrewards;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.dexrewards.commands.DexCommands;
import me.unariginal.dexrewards.config.*;
import me.unariginal.dexrewards.handlers.PokedexProgressHandler;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.handlers.EventHandler;
import me.unariginal.dexrewards.placeholders.PlaceholderManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.unariginal.dexrewards.config.ConfigManager.CONFIG;
import static me.unariginal.dexrewards.config.ConfigManager.DEX_TYPES;

public class DexRewards implements ModInitializer {
    public final static String MOD_ID = "dexrewards";
    public final static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ExecutorService saveExecutor;
    public static DexRewards INSTANCE;

    public FabricServerAudiences audience;
    public MinecraftServer server;

    public static Map<String, Integer> dexTypeTotals = new HashMap<>();

    @Override
    public void onInitialize() {
        INSTANCE = this;

        CommandRegistrationCallback.EVENT.register(DexCommands::registerCommands);
        EventHandler.registerEvents();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.audience = FabricServerAudiences.of(server);
            saveExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "DexRewards-Save"));

            reload();
            PlaceholderManager.registerPlaceholders();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            saveExecutor.shutdown();
            try {
                if (!saveExecutor.awaitTermination(10, TimeUnit.SECONDS))
                    LOGGER.warn("[DexRewards] Timed out waiting for player data saves to finish.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(event -> {
            ServerPlayerEntity player = event.getPlayer();
            PlayerDataConfig.loadPlayerData(player);
            PlayerData playerData = PlayerDataConfig.getPlayerData(player.getUuid());
            if (playerData != null) {
                for (Pokemon p : Cobblemon.INSTANCE.getStorage().getParty(player))
                    PokedexProgressHandler.recordCaughtShiny(playerData, p);
                for (Pokemon p : Cobblemon.INSTANCE.getStorage().getPC(player))
                    PokedexProgressHandler.recordCaughtShiny(playerData, p);

                playerData.updateProgressCount();
                playerData.updateClaimableRewards();
                PlayerDataConfig.savePlayerData(player);
            }
        });

        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(event -> {
            ServerPlayerEntity player = event.getPlayer();
            PlayerDataConfig.savePlayerData(player);
            PlayerDataConfig.playerDataMap.remove(player.getUuid());
        });
    }

    public static void reload() {
        ConfigManager.load();
        dexTypeTotals.clear();

        for (DexTypeConfig dexTypeConfig : DEX_TYPES.values()) {
            dexTypeConfig.validEntries = PokedexProgressHandler.getDexEntries(dexTypeConfig).stream()
                    .filter(entry -> PokedexProgressHandler.isValidSpecies(dexTypeConfig, entry.getSpeciesId()))
                    .toList();

            Set<String> ignored = dexTypeConfig.ignoredForms.stream()
                    .map(f -> f.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
            dexTypeConfig.validForms = dexTypeConfig.validEntries.stream()
                    .flatMap(entry -> entry.getForms().stream()
                            .filter(form -> !ignored.contains(form.getDisplayForm().toLowerCase(Locale.ROOT)))
                            .map(form -> new PokedexProgressHandler.FormTarget(entry.getSpeciesId(), form)))
                    .toList();

            int total = dexTypeConfig.requireAllForms ? dexTypeConfig.validForms.size() : dexTypeConfig.validEntries.size();
            dexTypeTotals.put(dexTypeConfig.dexTypeId, total);
        }

        for (ServerPlayerEntity player : INSTANCE.server.getPlayerManager().getPlayerList()) {
            PlayerData playerData = PlayerDataConfig.getPlayerData(player.getUuid());

            if (playerData != null) {
                playerData.updateProgressCount();
                playerData.updateClaimableRewards();
                PlayerDataConfig.savePlayerData(player.getUuid());
            }
        }
    }

    public void logInfo(String message) {
        if (CONFIG.debug) {
            LOGGER.info(message);
        }
    }
}
