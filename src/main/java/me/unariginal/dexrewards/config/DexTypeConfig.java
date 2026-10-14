package me.unariginal.dexrewards.config;

import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.handlers.PokedexProgressHandler;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DexTypeConfig {
    public transient String dexTypeId = "national";
    public transient Identifier pokedexId;
    public transient List<PokedexEntry> validEntries = new ArrayList<>();
    public transient List<PokedexProgressHandler.FormTarget> validForms = new ArrayList<>();

    public String displayName = "National Dex";
    public String pokedex = "cobblemon:national";
    public boolean requireCaught = true;
    public boolean requireShiny = false;
    public boolean requireAllForms = false;
    public List<String> validSpecies = new ArrayList<>();
    public List<String> ignoredSpecies = new ArrayList<>();
    public List<String> validLabels = new ArrayList<>();
    public List<String> ignoredLabels = new ArrayList<>();
    public List<String> ignoredForms = new ArrayList<>();
    public Map<String, RewardGroup> rewardGroups = new HashMap<>();
}