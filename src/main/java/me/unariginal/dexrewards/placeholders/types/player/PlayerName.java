package me.unariginal.dexrewards.placeholders.types.player;

import me.unariginal.dexrewards.placeholders.GenericResult;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerPlaceholder;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class PlayerName implements PlayerPlaceholder {
    @Override
    public GenericResult handle(ServerPlayerEntity player, List<String> args) {
        return GenericResult.valid(player.getNameForScoreboard());
    }

    @Override
    public List<String> id() {
        return List.of("player");
    }
}
