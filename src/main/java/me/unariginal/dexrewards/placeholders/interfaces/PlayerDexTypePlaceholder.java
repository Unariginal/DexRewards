package me.unariginal.dexrewards.placeholders.interfaces;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.placeholders.GenericResult;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public interface PlayerDexTypePlaceholder {
    GenericResult handle(ServerPlayerEntity player, DexTypeConfig dexTypeConfig, List<String> args);
    List<String> id();
}
