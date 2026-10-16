package me.unariginal.dexrewards.config;

import me.unariginal.dexrewards.data.gui.BaseGui;
import me.unariginal.dexrewards.data.gui.GuiElement;

public class RewardGuiConfig extends BaseGui {
    public GuiElement playerInfoItem;
    public GroupsElement groupItem;

    public static class GroupsElement extends GuiElement {
        public GroupStatusElement claimed;
        public GroupStatusElement claimable;
        public GroupStatusElement locked;

        public static class GroupStatusElement extends GuiElement {
            public boolean showEnchantmentGlint = false;
        }
    }
}
