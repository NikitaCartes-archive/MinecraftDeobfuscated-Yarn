/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ContainerListener {
    public void onContainerRegistered(Container var1, DefaultedList<ItemStack> var2);

    public void onContainerSlotUpdate(Container var1, int var2, ItemStack var3);

    public void onContainerPropertyUpdate(Container var1, int var2, int var3);
}

