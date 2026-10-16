package me.unariginal.dexrewards.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.unariginal.dexrewards.DexRewards;
import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.config.PlayerDataConfig;
import me.unariginal.dexrewards.data.PlayerData;
import me.unariginal.dexrewards.data.rewards.RewardGroup;
import me.unariginal.dexrewards.placeholders.ParseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static me.unariginal.dexrewards.config.ConfigManager.MESSAGES;
import static me.unariginal.dexrewards.config.ConfigManager.REWARD_GUI;
import static me.unariginal.dexrewards.utils.TextUtils.deserialize;
import static me.unariginal.dexrewards.utils.TextUtils.deserializeLore;

public class RewardsGui extends PagedGui {
    private final DexTypeConfig dexTypeConfig;

    public RewardsGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots, DexTypeConfig dexTypeConfig) {
        super(type, player, manipulatePlayerSlots);
        this.dexTypeConfig = dexTypeConfig;
        updateDisplay();
    }

    @Override
    public void updateDisplay() {
        PlayerData playerData = PlayerDataConfig.getPlayerData(player.getUuid());
        if (playerData == null) return;
        PlayerData.ProgressTracker progressTracker = playerData.pokedexProgress.get(dexTypeConfig.dexTypeId);
        if (progressTracker == null) return;

        ParseContext parseContext = ParseContext.builder().player(player).dexType(dexTypeConfig).build();

        setTitle(deserialize(REWARD_GUI.title, parseContext));

        REWARD_GUI.getSlotsBySymbol(REWARD_GUI.backgroundItem.symbol).forEach(slot -> {
            ItemStack icon = REWARD_GUI.backgroundItem.item != null ? REWARD_GUI.backgroundItem.item : ItemStack.EMPTY;
            setSlot(slot, new GuiElementBuilder(icon.copy())
                    .setName(deserialize(REWARD_GUI.backgroundItem.itemName, parseContext))
                    .setLore(deserializeLore(REWARD_GUI.backgroundItem.itemLore, parseContext))
                    .build());
        });

        REWARD_GUI.getSlotsBySymbol(REWARD_GUI.playerInfoItem.symbol).forEach(slot -> setSlot(slot, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(deserialize(REWARD_GUI.playerInfoItem.itemName, parseContext))
                .setLore(deserializeLore(REWARD_GUI.playerInfoItem.itemLore, parseContext))
                .setSkullOwner(player.getGameProfile(), DexRewards.INSTANCE.server)
                .build()));

        int pageSize = getSinglePageSize();
        List<Integer> groupSlots = REWARD_GUI.getSlotsBySymbol(REWARD_GUI.groupItem.symbol);
        List<RewardGroup> rewardGroups = dexTypeConfig.rewardGroups.values().stream().toList();
        int index = getPage() * groupSlots.size();
        for (int i = 0; i < pageSize; i++) {
            if (groupSlots.contains(i)) {
                if (rewardGroups.size() > index) {
                    RewardGroup rewardGroup = rewardGroups.get(index);
                    ParseContext groupParseContext = ParseContext.builder().player(player).dexType(dexTypeConfig).rewardGroup(rewardGroup).build();

                    GuiElementBuilder guiElementBuilder = new GuiElementBuilder(rewardGroup.iconItem.copy());

                    if (progressTracker.claimableRewards.contains(rewardGroup.rewardGroupId)) {
                        guiElementBuilder.setName(deserialize(REWARD_GUI.groupItem.claimable.itemName, groupParseContext));
                        guiElementBuilder.setLore(deserializeLore(rewardGroup.iconLore, groupParseContext));
                        guiElementBuilder.glow(REWARD_GUI.groupItem.claimable.showEnchantmentGlint);
                        guiElementBuilder.setCallback(clickType -> {
                            if (progressTracker.claimableRewards.remove(rewardGroup.rewardGroupId)) {
                                progressTracker.claimedRewards.add(rewardGroup.rewardGroupId);
                                rewardGroup.distributeRewards(player);
                                PlayerDataConfig.savePlayerData(player);
                                player.sendMessage(deserialize(MESSAGES.rewardsClaimed, groupParseContext));
                            }
                            updateDisplay();
                        });
                    } else if (progressTracker.claimedRewards.contains(rewardGroup.rewardGroupId)) {
                        guiElementBuilder.setName(deserialize(REWARD_GUI.groupItem.claimed.itemName, groupParseContext));
                        guiElementBuilder.setLore(deserializeLore(rewardGroup.iconLore, groupParseContext));
                        guiElementBuilder.glow(REWARD_GUI.groupItem.claimed.showEnchantmentGlint);
                    } else {
                        if (REWARD_GUI.groupItem.locked.item != null)
                            guiElementBuilder.setItem(REWARD_GUI.groupItem.locked.item.getItem());
                        guiElementBuilder.setName(deserialize(REWARD_GUI.groupItem.locked.itemName, groupParseContext));
                        guiElementBuilder.setLore(deserializeLore(rewardGroup.iconLore, groupParseContext));
                        guiElementBuilder.glow(REWARD_GUI.groupItem.locked.showEnchantmentGlint);
                    }

                    setSlot(i, guiElementBuilder.build());
                } else {
                    ItemStack icon = REWARD_GUI.backgroundItem.item != null ? REWARD_GUI.backgroundItem.item : ItemStack.EMPTY;
                    setSlot(i, new GuiElementBuilder(icon.copy())
                            .setName(deserialize(REWARD_GUI.backgroundItem.itemName, parseContext))
                            .setLore(deserializeLore(REWARD_GUI.backgroundItem.itemLore, parseContext))
                            .build());
                }
                index++;
            }
        }

        REWARD_GUI.getSlotsBySymbol(REWARD_GUI.nextItem.symbol).forEach(slot -> {
            if (canNextPage()) {
                ItemStack icon = REWARD_GUI.nextItem.item != null ? REWARD_GUI.nextItem.item : ItemStack.EMPTY;
                setSlot(slot, new GuiElementBuilder(icon.copy())
                        .setName(deserialize(REWARD_GUI.nextItem.itemName, parseContext))
                        .setLore(deserializeLore(REWARD_GUI.nextItem.itemLore, parseContext))
                        .setCallback(clickType -> nextPage())
                        .build());
            } else {
                ItemStack icon = REWARD_GUI.backgroundItem.item != null ? REWARD_GUI.backgroundItem.item : ItemStack.EMPTY;
                setSlot(slot, new GuiElementBuilder(icon.copy())
                        .setName(deserialize(REWARD_GUI.backgroundItem.itemName, parseContext))
                        .setLore(deserializeLore(REWARD_GUI.backgroundItem.itemLore, parseContext))
                        .build());
            }
        });

        REWARD_GUI.getSlotsBySymbol(REWARD_GUI.previousItem.symbol).forEach(slot -> {
            if (canPreviousPage()) {
                ItemStack icon = REWARD_GUI.previousItem.item != null ? REWARD_GUI.previousItem.item : ItemStack.EMPTY;
                setSlot(slot, new GuiElementBuilder(icon.copy())
                        .setName(deserialize(REWARD_GUI.previousItem.itemName, parseContext))
                        .setLore(deserializeLore(REWARD_GUI.previousItem.itemLore, parseContext))
                        .setCallback(clickType -> previousPage())
                        .build());
            } else {
                ItemStack icon = REWARD_GUI.backgroundItem.item != null ? REWARD_GUI.backgroundItem.item : ItemStack.EMPTY;
                setSlot(slot, new GuiElementBuilder(icon.copy())
                        .setName(deserialize(REWARD_GUI.backgroundItem.itemName, parseContext))
                        .setLore(deserializeLore(REWARD_GUI.backgroundItem.itemLore, parseContext))
                        .build());
            }
        });

        REWARD_GUI.getSlotsBySymbol(REWARD_GUI.closeItem.symbol).forEach(slot -> {
            ItemStack icon = REWARD_GUI.closeItem.item != null ? REWARD_GUI.closeItem.item : ItemStack.EMPTY;
            setSlot(slot, new GuiElementBuilder(icon.copy())
                    .setName(deserialize(REWARD_GUI.closeItem.itemName, parseContext))
                    .setLore(deserializeLore(REWARD_GUI.closeItem.itemLore, parseContext))
                    .setCallback(clickType -> close())
                    .build());
        });
    }

    @Override
    protected int getPageAmount() {
        return REWARD_GUI.getPageTotal(dexTypeConfig.rewardGroups.size(), REWARD_GUI.groupItem.symbol);
    }
}
