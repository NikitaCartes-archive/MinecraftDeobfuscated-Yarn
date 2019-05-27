/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class BedItem
extends BlockItem {
    public BedItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    protected boolean place(ItemPlacementContext itemPlacementContext, BlockState blockState) {
        return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 26);
    }
}

