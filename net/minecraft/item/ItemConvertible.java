/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.item.Item;

/**
 * Represents an object that has an item form.
 */
public interface ItemConvertible {
    /**
     * Gets this object in its item form.
     */
    public Item asItem();
}

