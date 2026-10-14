package me.unariginal.dexrewards.config;

import me.unariginal.dexrewards.data.gui.BaseGui;
import me.unariginal.dexrewards.data.gui.GuiElement;

public class RewardGuiConfig extends BaseGui {
    public GuiElement playerInfoItem;
    public GroupsElement groupItem;

    public static class GroupsElement extends GuiElement {
        public GuiElement claimed;
        public GuiElement claimable;
        public GuiElement locked;
    }
}
