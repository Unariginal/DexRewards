package me.unariginal.dexrewards.utils;

import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.data.rewards.Reward;
import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.placeholders.ParseContext;
import me.unariginal.dexrewards.placeholders.PlaceholderManager;
import me.unariginal.dexrewards.placeholders.interfaces.DexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerDexTypePlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.PlayerPlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.RewardGroupPlaceholder;
import me.unariginal.dexrewards.placeholders.interfaces.ServerPlaceholder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.unariginal.dexrewards.placeholders.PlaceholderManager.*;

public class TextUtils {
    private static final Pattern pattern = Pattern.compile("%([A-Za-z0-9_.-]+(?: [^%]+)?)%");

    public static List<Text> deserializeLore(List<String> lore, ParseContext parseContext) {
        List<Text> returnLore = new ArrayList<>();
        lore.forEach(line -> returnLore.add(deserialize(line, parseContext)));
        return returnLore;
    }

    public static Text deserialize(String text) {
        return deserialize(text, ParseContext.builder().build());
    }

    public static Text deserialize(String text, ParseContext parseContext) {
        text = parse(text, parseContext);
        return DexRewards.INSTANCE.audience.toNative(MiniMessage.miniMessage().deserialize(text));
    }

    public static String parse(String text, ParseContext parseContext) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String[] parts = matcher.group(1).split(" ");
            String id = parts[0];
            List<String> args = Arrays.asList(parts).subList(1, parts.length);

            String replacement = resolve(id, args, parseContext);
            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static String parse(String text) {
        return parse(text, ParseContext.builder().build());
    }

    @Nullable
    private static String resolve(String id, List<String> args, ParseContext parseContext) {
        for (ServerPlaceholder placeholder : serverPlaceholders) {
            if (placeholder.id().contains(id)) return placeholder.handle(args).string;
        }

        // The dex type can come from an argument (%pokedex_total kanto%), the context, or the default dex type
        for (DexTypePlaceholder placeholder : dexTypePlaceholders) {
            if (!placeholder.id().contains(id)) continue;
            DexTypeConfig dexTypeConfig = PlaceholderManager.resolveDexType(parseContext.getDexType(), args);
            return dexTypeConfig != null ? placeholder.handle(dexTypeConfig, args).string : null;
        }

        ServerPlayerEntity player = parseContext.getPlayer();
        if (player != null) {
            for (PlayerPlaceholder placeholder : playerPlaceholders) {
                if (placeholder.id().contains(id)) return placeholder.handle(player, args).string;
            }

            for (PlayerDexTypePlaceholder placeholder : playerDexTypePlaceholders) {
                if (!placeholder.id().contains(id)) continue;
                DexTypeConfig dexTypeConfig = PlaceholderManager.resolveDexType(parseContext.getDexType(), args);
                return dexTypeConfig != null ? placeholder.handle(player, dexTypeConfig, args).string : null;
            }
        }

        RewardGroup rewardGroup = parseContext.getRewardGroup();
        if (rewardGroup != null) {
            for (RewardGroupPlaceholder placeholder : rewardGroupPlaceholders) {
                if (placeholder.id().contains(id)) return placeholder.handle(rewardGroup, args).string;
            }
        }

        return null;
    }

    public static String formatPercent(double percent) {
        return new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(percent);
    }

    public static String parse(String text, Reward reward) {
        return parse(text)
                .replace("%reward%", reward.displayName);
    }

    public static String parse(String text, ItemStack itemStack) {
        return parse(text)
                .replace("%item%", itemStack.getName().getString())
                .replace("%item_count%", String.valueOf(itemStack.getCount()));
    }
}
