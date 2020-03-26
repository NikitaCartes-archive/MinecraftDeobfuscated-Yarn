/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * A special inventory interface for inventories that expose different slots for different sides, such as hoppers.
 */
public interface SidedInventory
extends Inventory {
    /**
     * Gets the available slot positions that are reachable from a given side.
     */
    public int[] getAvailableSlots(Direction var1);

    /**
     * Determines whether the given stack can be inserted into this inventory at the specified slot position from the given direction.
     */
    public boolean canInsert(int var1, ItemStack var2, @Nullable Direction var3);

    /**
     * Determines whether the given stack can be removed from this inventory at the specified slot position from the given direction.
     */
    public boolean canExtract(int var1, ItemStack var2, Direction var3);
}

