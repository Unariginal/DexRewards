package me.unariginal.dexrewards.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.util.adapters.PokemonPropertiesAdapter;
import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.data.rewards.CommandReward;
import me.unariginal.dexrewards.data.rewards.ItemReward;
import me.unariginal.dexrewards.data.rewards.PokemonReward;
import me.unariginal.dexrewards.data.rewards.Reward;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.io.IOException;

public class GsonUtils {
    public static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter())
            .registerTypeAdapter(PokemonProperties.class, new PokemonPropertiesAdapter(true))
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(Reward.class, "type")
                            .registerSubtype(ItemReward.class, "item")
                            .registerSubtype(CommandReward.class, "command")
                            .registerSubtype(PokemonReward.class, "pokemon")
            )
            .create();

    public static class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
        @Override
        public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
            if (itemStack == null) {
                jsonWriter.nullValue();
                return;
            }
            JsonElement element;
            try {
                DataResult<JsonElement> result = ItemStack.OPTIONAL_CODEC.encodeStart(DexRewards.INSTANCE.server.getRegistryManager().getOps(JsonOps.INSTANCE), itemStack);

                element = result.getOrThrow();
            } catch (IllegalStateException e) {
                // This is from minecraft:air, so let's manually set it to air in this case!
                JsonObject object = new JsonObject();
                object.addProperty("id", "minecraft:air");
                element = object;
            }

            Streams.write(element, jsonWriter);
        }

        @Override
        public ItemStack read(JsonReader jsonReader) {
            JsonElement element = JsonParser.parseReader(jsonReader);

            ItemStack itemStack;
            try {
                DataResult<ItemStack> result = ItemStack.OPTIONAL_CODEC.parse(DexRewards.INSTANCE.server.getRegistryManager().getOps(JsonOps.INSTANCE), element);

                itemStack = result.getOrThrow();
            } catch (IllegalStateException e) {
                DexRewards.LOGGER.warn("[DexRewards] Couldn't parse item {}, defaulting to AIR: {}", element, e.getMessage());
                itemStack = Items.AIR.getDefaultStack();
            }

            return itemStack;
        }
    }
}
