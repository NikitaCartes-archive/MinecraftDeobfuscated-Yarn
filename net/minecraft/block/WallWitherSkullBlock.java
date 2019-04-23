/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WallWitherSkullBlock
extends WallSkullBlock {
    protected WallWitherSkullBlock(Block.Settings settings) {
        super(SkullBlock.Type.WITHER_SKELETON, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        Blocks.WITHER_SKELETON_SKULL.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
    }
}

