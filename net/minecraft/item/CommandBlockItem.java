/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import org.jetbrains.annotations.Nullable;

public class CommandBlockItem
extends BlockItem {
    public CommandBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    @Nullable
    protected BlockState getBlockState(ItemPlacementContext itemPlacementContext) {
        PlayerEntity playerEntity = itemPlacementContext.getPlayer();
        return playerEntity == null || playerEntity.isCreativeLevelTwoOp() ? super.getBlockState(itemPlacementContext) : null;
    }
}

