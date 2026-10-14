package me.unariginal.dexrewards.commands.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.dexrewards.config.PlayerDataConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.placeholders.ParseContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.unariginal.dexrewards.config.ConfigManager.MESSAGES;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DexUpdateCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("update")
                .requires(Permissions.require("dexrewards.update", 4))
                .then(argument("player", EntityArgumentType.player())
                        .executes(DexUpdateCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendMessage(Text.literal("Invalid Player Argument").formatted(Formatting.RED));
            return 0;
        }

        if (player != null) {
            PlayerData data = PlayerDataConfig.getPlayerData(player.getUuid());
            if (data != null) {
                data.updateProgressCount();
                data.updateClaimableRewards();

                PlayerDataConfig.savePlayerData(player);
                ctx.getSource().sendMessage(deserialize(MESSAGES.updateCommand, ParseContext.builder().player(player).build()));
            }
        }
        return 1;
    }
}
