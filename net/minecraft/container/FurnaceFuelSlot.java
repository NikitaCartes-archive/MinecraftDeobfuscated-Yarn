/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot
extends Slot {
    private final AbstractFurnaceContainer container;

    public FurnaceFuelSlot(AbstractFurnaceContainer abstractFurnaceContainer, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
        this.container = abstractFurnaceContainer;
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return this.container.isFuel(itemStack) || FurnaceFuelSlot.isBucket(itemStack);
    }

    @Override
    public int getMaxStackAmount(ItemStack itemStack) {
        return FurnaceFuelSlot.isBucket(itemStack) ? 1 : super.getMaxStackAmount(itemStack);
    }

    public static boolean isBucket(ItemStack itemStack) {
        return itemStack.getItem() == Items.BUCKET;
    }
}

