package me.unariginal.dexrewards.data.gui;

import net.minecraft.screen.ScreenHandlerType;

import java.util.ArrayList;
import java.util.List;

public class BaseGui {
    public String title;
    public Boolean useHopperGui;
    public int rows;
    public List<String> guiLayout = new ArrayList<>();
    public GuiElement backgroundItem;
    public GuiElement previousItem;
    public GuiElement nextItem;
    public GuiElement closeItem;

    public ScreenHandlerType<?> getScreenHandler() {
        if (useHopperGui != null && useHopperGui) return ScreenHandlerType.HOPPER;
        return switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            default -> ScreenHandlerType.GENERIC_9X6;
        };
    }

    public List<Integer> getSlotsBySymbol(String symbol) {
        List<Integer> returnSlots = new ArrayList<>();
        int slotCount = 0;
        for (String row : guiLayout) {
            for (char slot : row.toCharArray()) {
                if (slot == symbol.charAt(0)) {
                    returnSlots.add(slotCount);
                }
                slotCount++;
            }
        }
        return returnSlots;
    }

    public int getTotalSlotsBySymbol(String symbol) {
        int count = 0;
        for (String row : guiLayout) {
            for (char slot : row.toCharArray()) {
                if (slot == symbol.charAt(0)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int getPageTotal(int totalElements, int size) {
        return (int) Math.ceil((double) totalElements / size);
    }

    public int getPageTotal(int totalElements, String symbol) {
        return getPageTotal(totalElements, getTotalSlotsBySymbol(symbol));
    }
}
