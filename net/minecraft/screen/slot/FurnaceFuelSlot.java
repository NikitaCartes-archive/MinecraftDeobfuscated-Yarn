/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FurnaceFuelSlot
extends Slot {
    private final AbstractFurnaceScreenHandler handler;

    public FurnaceFuelSlot(AbstractFurnaceScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.handler.isFuel(stack) || FurnaceFuelSlot.isBucket(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.getItem() == Items.BUCKET;
    }
}

