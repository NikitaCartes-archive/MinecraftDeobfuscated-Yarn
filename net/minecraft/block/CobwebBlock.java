/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CobwebBlock
extends Block {
    public CobwebBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        entity.slowMovement(blockState, new Vec3d(0.25, 0.05f, 0.25));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}

