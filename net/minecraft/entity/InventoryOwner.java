/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.util.annotation.Debug;

public interface InventoryOwner {
    @Debug
    public Inventory getInventory();
}

