/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public interface ScreenHandlerListener {
    public void onSlotUpdate(ScreenHandler var1, int var2, ItemStack var3);

    public void onPropertyUpdate(ScreenHandler var1, int var2, int var3);
}

