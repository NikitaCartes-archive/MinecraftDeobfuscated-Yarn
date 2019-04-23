/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class AliasedBlockItem
extends BlockItem {
    public AliasedBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }
}

