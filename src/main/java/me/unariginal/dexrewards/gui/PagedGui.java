package me.unariginal.dexrewards.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class PagedGui extends SimpleGui {
    private static final Object2IntMap<ScreenHandlerType<?>> TYPE_TO_SIZE = new Object2IntOpenHashMap<>();

    protected int page = 0;

    public PagedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
    }

    protected void setPage(int page) {
        this.page = Math.clamp(page, 0, Math.max(0, this.getPageAmount() - 1));
        this.updateDisplay();
    }

    protected void nextPage() {
        setPage(this.page + 1);
    }

    protected boolean canNextPage() {
        return this.getPageAmount() > this.page + 1;
    }

    protected void previousPage() {
        setPage(this.page - 1);
    }

    protected boolean canPreviousPage() {
        return this.page - 1 >= 0;
    }

    protected abstract void updateDisplay();

    protected int getPage() {
        return this.page;
    }

    public final int getSinglePageSize() {
        if (type.equals(ScreenHandlerType.HOPPER)) return 5;
        return 9 * (TYPE_TO_SIZE.getInt(this.type) + (this.isIncludingPlayer() ? 4 : 0));
    }

    protected abstract int getPageAmount();

    static {
        TYPE_TO_SIZE.defaultReturnValue(1);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X2, 2);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X3, 3);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X4, 4);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X5, 5);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X6, 6);
    }
}
