/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NetherStarItem
extends Item {
    public NetherStarItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return true;
    }
}

