/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BookItem
extends Item {
    public BookItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return itemStack.getAmount() == 1;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

