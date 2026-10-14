package me.unariginal.dexrewards.handlers;

import com.cobblemon.mod.common.api.pokedex.*;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.data.PlayerData;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static me.unariginal.dexrewards.config.ConfigManager.CONFIG;

public class PokedexProgressHandler {
    public record FormTarget(Identifier speciesId, PokedexForm form) {}

    public static int countProgress(@NotNull DexTypeConfig dexTypeConfig, @NotNull AbstractPokedexManager pokedexManager, @NotNull PlayerData data) {
        if (dexTypeConfig.requireAllForms) {
            int count = 0;
            for (FormTarget target : dexTypeConfig.validForms) {
                if (formCounts(dexTypeConfig, pokedexManager, data, target)) count++;
            }
            return count;
        }

        int count = 0;
        for (PokedexEntry entry : dexTypeConfig.validEntries) {
            boolean counts;
            if (dexTypeConfig.requireCaught && dexTypeConfig.requireShiny) {
                counts = data.hasCaughtShiny(entry.getSpeciesId());
            } else {
                PokedexEntryProgress progress = pokedexManager.getKnowledgeForSpecies(entry.getSpeciesId());
                counts = dexTypeConfig.requireCaught
                        ? progress == PokedexEntryProgress.OWNED
                        : progress != PokedexEntryProgress.UNREGISTERED;
                if (counts && dexTypeConfig.requireShiny) counts = hasSeenShiny(pokedexManager, entry);
            }
            if (counts) count++;
        }
        return count;
    }

    private static boolean formCounts(DexTypeConfig dexTypeConfig, AbstractPokedexManager manager, PlayerData data, FormTarget target) {
        if (dexTypeConfig.requireCaught && dexTypeConfig.requireShiny) {
            for (String unlock : target.form().getUnlockForms()) {
                if (data.hasCaughtShiny(target.speciesId(), unlock)) return true;
            }
            return false;
        }

        SpeciesDexRecord species = manager.getSpeciesRecord(target.speciesId());
        if (species == null) return false;
        for (String unlock : target.form().getUnlockForms()) {
            FormDexRecord record = species.getFormRecord(unlock);
            if (record == null) continue;
            boolean counts = dexTypeConfig.requireCaught
                    ? record.getKnowledge() == PokedexEntryProgress.OWNED
                    : record.getKnowledge() != PokedexEntryProgress.UNREGISTERED;
            if (counts && dexTypeConfig.requireShiny) counts = record.hasSeenShinyState(true);
            if (counts) return true;
        }
        return false;
    }

    public static List<PokedexEntry> getDexEntries(@NotNull DexTypeConfig dexTypeConfig) {
        var pokedex = Dexes.INSTANCE.getDexEntryMap().get(dexTypeConfig.pokedexId);
        return pokedex != null ? pokedex.getEntries() : List.of();
    }

    public static boolean hasSeenShiny(@NotNull AbstractPokedexManager dexManager, @NotNull PokedexEntry entry) {
        SpeciesDexRecord record = dexManager.getSpeciesRecord(entry.getSpeciesId());
        if (record == null) return false;
        for (PokedexForm form : entry.getForms()) {
            FormDexRecord formDexRecord = record.getFormRecord(form.getDisplayForm());
            if (formDexRecord != null && formDexRecord.hasSeenShinyState(true)) return true;
        }
        return false;
    }

    public static boolean recordCaughtShiny(PlayerData data, Pokemon pokemon) {
        if (!pokemon.getShiny()) return false;
        return data.addCaughtShiny(pokemon.getSpecies().getResourceIdentifier(), pokemon.getForm().getName());
    }

    public static boolean isValidSpecies(@NotNull DexTypeConfig dexTypeConfig, @NotNull Identifier speciesId) {
        Species species = PokemonSpecies.getByIdentifier(speciesId);
        if (species != null) {
            if (!CONFIG.implementedOnly || species.getImplemented()) {
                if (!dexTypeConfig.validSpecies.isEmpty() && !dexTypeConfig.validSpecies.contains(species.showdownId().toLowerCase(Locale.ROOT))) {
                    return false;
                }
                if (dexTypeConfig.ignoredSpecies.contains(species.showdownId().toLowerCase(Locale.ROOT))) {
                    return false;
                }
                if (!dexTypeConfig.validLabels.isEmpty()) {
                    boolean match = false;
                    for (String label : dexTypeConfig.validLabels) {
                        if (species.getLabels().contains(label)) {
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        return false;
                    }
                }

                for (String label : dexTypeConfig.ignoredLabels) {
                    if (species.getLabels().contains(label)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return CONFIG.allowInvalidSpecies;
        }

        return true;
    }
}
