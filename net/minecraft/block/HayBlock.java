/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock
extends PillarBlock {
    public HayBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(AXIS, Direction.Axis.Y));
    }

    @Override
    public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
        entity.handleFallDamage(f, 0.2f);
    }
}

