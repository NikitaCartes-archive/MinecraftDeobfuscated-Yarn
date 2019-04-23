/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class TallBlockItem
extends BlockItem {
    public TallBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    protected boolean setBlockState(ItemPlacementContext itemPlacementContext, BlockState blockState) {
        itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos().up(), Blocks.AIR.getDefaultState(), 27);
        return super.setBlockState(itemPlacementContext, blockState);
    }
}

