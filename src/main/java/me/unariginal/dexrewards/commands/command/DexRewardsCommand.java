package me.unariginal.dexrewards.commands.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.gui.RewardsGui;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.atomic.AtomicReference;

import static me.unariginal.dexrewards.config.ConfigManager.*;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DexRewardsCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("rewards")
                .requires(Permissions.require("dexrewards.rewards", true))
                .executes(DexRewardsCommand::execute)
                .then(argument("dex-type", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            DEX_TYPES.keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(DexRewardsCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player != null) {
            if (DEX_TYPES.isEmpty()) {
                DexRewards.LOGGER.error("[DexRewards] Can't open rewards gui! No registered dex types.");
                player.sendMessage(deserialize("<red>No registered dex types."));
                return 0;
            }
            DexTypeConfig dexTypeConfig = null;
            AtomicReference<String> dexTypeIdRef = new AtomicReference<>(CONFIG.defaultDexType);
            try {
                dexTypeIdRef.set(StringArgumentType.getString(ctx, "dex-type"));
            } catch (IllegalArgumentException ignored) {
            }
            if (dexTypeIdRef.get() != null) dexTypeConfig = DEX_TYPES.get(dexTypeIdRef.get());
            if (dexTypeConfig == null) dexTypeConfig = DEX_TYPES.values().stream().toList().getFirst();

            RewardsGui rewardsGui = new RewardsGui(REWARD_GUI.getScreenHandler(), player, false, dexTypeConfig);
            rewardsGui.open();
        }
        return 1;
    }
}
