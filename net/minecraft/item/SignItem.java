/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SignItem
extends WallStandingBlockItem {
    public SignItem(Item.Settings settings, Block block, Block block2) {
        super(block, block2, settings);
    }

    @Override
    protected boolean afterBlockPlaced(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
        boolean bl = super.afterBlockPlaced(blockPos, world, playerEntity, itemStack, blockState);
        if (!world.isClient && !bl && playerEntity != null) {
            playerEntity.openEditSignScreen((SignBlockEntity)world.getBlockEntity(blockPos));
        }
        return bl;
    }
}

