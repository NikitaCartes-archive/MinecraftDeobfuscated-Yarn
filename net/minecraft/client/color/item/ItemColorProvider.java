/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public interface ItemColorProvider {
    /**
     * {@return the color of the item stack for the specified tint index,
     * or -1 if not tinted}
     */
    public int getColor(ItemStack var1, int var2);
}

