package me.unariginal.dexrewards.commands.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.config.PlayerDataConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.placeholders.ParseContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.atomic.AtomicReference;

import static me.unariginal.dexrewards.config.ConfigManager.*;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DexResetCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("reset")
                .requires(Permissions.require("dexrewards.reset", 4))
                .then(argument("player", EntityArgumentType.player())
                        .then(argument("dex-type", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    DEX_TYPES.keySet().forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(DexResetCommand::execute)));
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendMessage(deserialize("<red>Invalid Player Argument"));
            return 0;
        }

        if (player != null) {
            DexTypeConfig dexTypeConfig = null;
            AtomicReference<String> dexTypeIdRef = new AtomicReference<>(CONFIG.defaultDexType);
            try {
                dexTypeIdRef.set(StringArgumentType.getString(ctx, "dex-type"));
            } catch (IllegalArgumentException ignored) {}
            if (dexTypeIdRef.get() != null) dexTypeConfig = DEX_TYPES.get(dexTypeIdRef.get());
            if (dexTypeConfig == null) {
                // Not a valid dex type, no defaults for the reset command
                return 0;
            }


            ParseContext parseContext = ParseContext.builder().player(player).dexType(dexTypeConfig).build();
            PlayerData data = PlayerDataConfig.getPlayerData(player.getUuid());
            if (data != null) {
                PlayerData.ProgressTracker tracker = data.pokedexProgress.computeIfAbsent(dexTypeConfig.dexTypeId, id -> new PlayerData.ProgressTracker());
                tracker.progressCount = 0;
                tracker.claimableRewards.clear();
                tracker.claimedRewards.clear();

                data.updateProgressCount();
                data.updateClaimableRewards();

                PlayerDataConfig.savePlayerData(player);
                ctx.getSource().sendMessage(deserialize(MESSAGES.resetCommandSender, parseContext));
                player.sendMessage(deserialize(MESSAGES.resetCommandTarget, parseContext));
            }
        }
        return 1;
    }
}
