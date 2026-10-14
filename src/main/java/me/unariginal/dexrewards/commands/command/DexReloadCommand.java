package me.unariginal.dexrewards.commands.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.dexrewards.DexRewards;
import net.minecraft.server.command.ServerCommandSource;

import static me.unariginal.dexrewards.config.ConfigManager.MESSAGES;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;
import static net.minecraft.server.command.CommandManager.literal;

public class DexReloadCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("reload")
                .requires(Permissions.require("dexrewards.reload", 4))
                .executes(DexReloadCommand::execute);
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) {
        DexRewards.reload();
        ctx.getSource().sendMessage(deserialize(MESSAGES.reloadCommand));
        return 1;
    }
}
