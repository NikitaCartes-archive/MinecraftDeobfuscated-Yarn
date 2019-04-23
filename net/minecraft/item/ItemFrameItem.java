/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameItem
extends DecorationItem {
    public ItemFrameItem(Item.Settings settings) {
        super(EntityType.ITEM_FRAME, settings);
    }

    @Override
    protected boolean canPlaceOn(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
        return !World.isHeightInvalid(blockPos) && playerEntity.canPlaceOn(blockPos, direction, itemStack);
    }
}

