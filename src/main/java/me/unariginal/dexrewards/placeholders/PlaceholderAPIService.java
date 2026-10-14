package me.unariginal.dexrewards.placeholders;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.placeholders.interfaces.DexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerDexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerPlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.ServerPlaceholder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.BiFunction;

import static me.unariginal.dexrewards.utils.TextUtils.parse;

public class PlaceholderAPIService {
    public void registerServer(ServerPlaceholder placeholder) {
        placeholder.id().forEach(id -> register(Identifier.of(DexRewards.MOD_ID, id), (ctx, args) -> placeholder.handle(args)));
    }

    public void registerPlayer(PlayerPlaceholder placeholder) {
        placeholder.id().forEach(id -> register(Identifier.of(DexRewards.MOD_ID, id), (ctx, args) -> {
            ServerPlayerEntity player = ctx.player();
            if (player == null) return GenericResult.invalid("NO PLAYER");
            return placeholder.handle(player, args);
        }));
    }

    // %dexrewards:<id> [dex type]%, falls back to the default dex type
    public void registerDexType(DexTypePlaceholder placeholder) {
        placeholder.id().forEach(id -> register(Identifier.of(DexRewards.MOD_ID, id), (ctx, args) -> {
            DexTypeConfig dexTypeConfig = PlaceholderManager.resolveDexType(null, args);
            if (dexTypeConfig == null) return GenericResult.invalid("UNKNOWN DEX TYPE");
            return placeholder.handle(dexTypeConfig, args);
        }));
    }

    // %dexrewards:<id> [dex type]%, falls back to the default dex type
    public void registerPlayerDexType(PlayerDexTypePlaceholder placeholder) {
        placeholder.id().forEach(id -> register(Identifier.of(DexRewards.MOD_ID, id), (ctx, args) -> {
            ServerPlayerEntity player = ctx.player();
            if (player == null) return GenericResult.invalid("NO PLAYER");
            DexTypeConfig dexTypeConfig = PlaceholderManager.resolveDexType(null, args);
            if (dexTypeConfig == null) return GenericResult.invalid("UNKNOWN DEX TYPE");
            return placeholder.handle(player, dexTypeConfig, args);
        }));
    }

    private void register(Identifier identifier, BiFunction<PlaceholderContext, List<String>, GenericResult> handler) {
        Placeholders.register(identifier, (ctx, arg) -> {
            try {
                List<String> args = arg != null ? List.of(parse(arg, ParseContext.builder().player(ctx.player()).build()).split(":")) : List.of();
                GenericResult result = handler.apply(ctx, args);
                if (result.isSuccessful) {
                    return PlaceholderResult.value(DexRewards.INSTANCE.audience.toNative(result.asComponent()));
                } else {
                    return PlaceholderResult.invalid(result.string);
                }
            } catch (Exception e) {
                DexRewards.LOGGER.error("[DexRewards] Placeholder '{}' failed to resolve", identifier, e);
                return PlaceholderResult.invalid("Error resolving placeholder");
            }
        });
    }
}
