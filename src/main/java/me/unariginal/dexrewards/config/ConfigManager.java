package me.unariginal.dexrewards.config;

import com.cobblemon.mod.common.api.pokedex.Dexes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.data.rewards.Reward;
import me.unariginal.dexrewards.data.rewards.RewardGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static me.unariginal.dexrewards.utils.GsonUtils.gson;

public class ConfigManager {
    public static File configDir;

    public static Config CONFIG;
    public static MessagesConfig MESSAGES;
    public static RewardGuiConfig REWARD_GUI;

    public static Map<String, DexTypeConfig> DEX_TYPES = new HashMap<>();

    public static void load() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("DexRewards").toFile();
        generateDefaultFiles();

        fillMissingWithDefaults("config.json", null, false);
        fillMissingWithDefaults("messages.json", null, false);
        fillMissingWithDefaults("reward_gui.json", null, false);

        CONFIG = loadOrFallback("config.json", Config.class, CONFIG);
        MESSAGES = loadOrFallback("messages.json", MessagesConfig.class, MESSAGES);
        REWARD_GUI = loadOrFallback("reward_gui.json", RewardGuiConfig.class, REWARD_GUI);

        loadDexTypes();
    }

    public static void loadDexTypes() {
        DEX_TYPES.clear();
        File dexTypesFolder = new File(configDir, "dex_types");
        File[] dexTypeFiles = dexTypesFolder.listFiles();
        if (dexTypeFiles != null) {
            for (File dexTypeFile : dexTypeFiles) {
                if (dexTypeFile.getName().endsWith(".json")) {
                    String fileName = "dex_types/" + dexTypeFile.getName();
                    DexTypeConfig dexTypeConfig = loadFile(fileName, DexTypeConfig.class);
                    if (dexTypeConfig != null) {
                        Identifier pokedexId = dexTypeConfig.pokedex != null ? Identifier.tryParse(dexTypeConfig.pokedex) : null;
                        if (pokedexId == null || !Dexes.INSTANCE.getDexEntryMap().containsKey(pokedexId)) {
                            DexRewards.LOGGER.error("[DexRewards] Dex type {} has an unknown pokedex '{}'. Skipping it until it is fixed.", fileName, dexTypeConfig.pokedex);
                            continue;
                        }
                        dexTypeConfig.pokedexId = pokedexId;

                        String dexTypeId = dexTypeFile.getName().replace(".json", "");
                        dexTypeConfig.dexTypeId = dexTypeId;
                        DEX_TYPES.put(dexTypeId, dexTypeConfig);

                        for (Map.Entry<String, RewardGroup> entry : dexTypeConfig.rewardGroups.entrySet()) {
                            entry.getValue().rewardGroupId = entry.getKey();
                            for (Map.Entry<String, Reward> rewardEntry : entry.getValue().rewards.entrySet()) {
                                rewardEntry.getValue().rewardId = rewardEntry.getKey();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void generateDefaultFiles() {
        generateDefaultFile("config.json");
        generateDefaultFile("messages.json");
        generateDefaultFile("reward_gui.json");
        File file = new File(configDir, "dex_types");
        if (!file.exists()) {
            generateDefaultFile("dex_types/national.json");
        } else {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                generateDefaultFile("dex_types/national.json");
            }
        }
    }

    private static <T> T loadOrFallback(String fileName, Class<T> clazz, @Nullable T previous) {
        T loaded = loadFile(fileName, clazz);
        if (loaded != null) return loaded;
        if (previous != null) {
            DexRewards.LOGGER.error("[DexRewards] Keeping the previously loaded {} until it is fixed.", fileName);
            return previous;
        }
        DexRewards.LOGGER.error("[DexRewards] Using the default {} until it is fixed.", fileName);
        return gson.fromJson(getDefaultJsonString(fileName), clazz);
    }

    @Nullable
    public static <T> Map<String, T> loadMapFile(String fileName, Class<T> clazz) {
        Type mapType = TypeToken.getParameterized(Map.class, String.class, clazz).getType();
        return loadFile(fileName, mapType);
    }

    @Nullable
    public static <T> T loadFile(String fileName, Class<T> clazz) {
        return loadFile(fileName, (Type) clazz);
    }

    @Nullable
    private static <T> T loadFile(String fileName, Type type) {
        File file = new File(configDir, fileName);
        if (!file.exists()) {
            DexRewards.LOGGER.error("[DexRewards] Error loading config file {}. File does not exist!", fileName);
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            T result = gson.fromJson(reader, type);
            if (result == null) DexRewards.LOGGER.error("[DexRewards] Config file {} is empty!", fileName);
            return result;
        } catch (IOException | JsonParseException e) {
            DexRewards.LOGGER.error("[DexRewards] Failed to load config file {}", fileName, e);
            return null;
        }
    }

    public static void generateDefaultFile(String fileName) {
        File file = new File(configDir, fileName);
        if (file.exists()) return;
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
            writeFile(file, getDefaultJsonString(fileName));
        } catch (IOException e) {
            DexRewards.LOGGER.error("[DexRewards] Failed to create directories for file {}", file.getName(), e);
        }
    }

    public static String getDefaultJsonString(String fileName) {
        try (InputStream in = DexRewards.class.getResourceAsStream("/dr_config/" + fileName)) {
            if (in == null) throw new IllegalStateException("Missing bundled default config " + fileName);
            return gson.toJson(JsonParser.parseReader(new InputStreamReader(in)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void fillMissingWithDefaults(String fileName, String defaultFileName, boolean isMapFile) {
        File file = new File(configDir, fileName);
        if (defaultFileName == null) defaultFileName = fileName;
        JsonObject defaultJson;
        JsonObject targetJson;
        try (InputStream in = DexRewards.class.getResourceAsStream("/dr_config/" + defaultFileName);
             FileReader fileReader = new FileReader(file)) {
            if (in == null) throw new IOException("Missing bundled default config " + defaultFileName);
            defaultJson = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
            targetJson = JsonParser.parseReader(fileReader).getAsJsonObject();
        } catch (IOException | JsonParseException | IllegalStateException e) {
            DexRewards.LOGGER.error("[DexRewards] Failed to parse {} for filling defaults.", fileName, e);
            return;
        }
        mergeJsonObjects(targetJson, defaultJson, isMapFile);
        writeFile(file, gson.toJson(targetJson));
    }

    private static void mergeJsonObjects(JsonObject target, JsonObject defaults, boolean isMapFile) {
        if (!isMapFile) {
            for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
                String key = entry.getKey();
                JsonElement defaultValue = entry.getValue();

                if (!target.has(key)) {
                    target.add(key, defaultValue.deepCopy());
                } else {
                    JsonElement targetValue = target.get(key);
                    if (!targetValue.isJsonArray() && targetValue.isJsonObject() && defaultValue.isJsonObject()) {
                        mergeJsonObjects(targetValue.getAsJsonObject(), defaultValue.getAsJsonObject(), false);
                    }
                }
            }
        } else {
            Map.Entry<String, JsonElement> defaultMapEntry = defaults.entrySet().stream().findFirst().isPresent() ? defaults.entrySet().stream().findFirst().get() : null;
            if (defaultMapEntry != null && defaultMapEntry.getValue().isJsonObject()) {
                for (Map.Entry<String, JsonElement> mapEntry : target.entrySet()) {
                    if (mapEntry.getValue().isJsonObject()) {
                        mergeJsonObjects(mapEntry.getValue().getAsJsonObject(), defaultMapEntry.getValue().getAsJsonObject(), false);
                    }
                }
            }
        }
    }

    public static void writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            DexRewards.LOGGER.error("[DexRewards] Failed to write to file {}", file.getName(), e);
        }
    }
}
