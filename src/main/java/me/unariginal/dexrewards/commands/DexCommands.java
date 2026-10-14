package me.unariginal.dexrewards.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.unariginal.dexrewards.commands.command.DexReloadCommand;
import me.unariginal.dexrewards.commands.command.DexResetCommand;
import me.unariginal.dexrewards.commands.command.DexRewardsCommand;
import me.unariginal.dexrewards.commands.command.DexUpdateCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class DexCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> commandNode = dispatcher.register(literal("dex")
                .then(DexReloadCommand.register())
                .then(DexRewardsCommand.register())
                .then(DexResetCommand.register())
                .then(DexUpdateCommand.register())
        );

        dispatcher.register(literal("dexrewards").redirect(commandNode));
    }
}
