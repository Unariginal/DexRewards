package me.unariginal.dexrewards.data.rewards;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Objects;

public class CommandReward extends Reward {
    public List<String> commands;

    @Override
    public void distributeReward(ServerPlayerEntity player) {
        CommandManager commandManager = Objects.requireNonNull(player.getServer()).getCommandManager();
        ServerCommandSource serverCommandSource = player.getServer().getCommandSource().withSilent();
        for (String command : commands) {
            commandManager.executeWithPrefix(serverCommandSource, command.replaceAll("%player%", player.getNameForScoreboard()));
        }
    }
}
