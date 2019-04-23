/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public interface FlyingItemEntity {
    public ItemStack getStack();
}

