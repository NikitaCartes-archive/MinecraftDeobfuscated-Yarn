/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BarrierBlock
extends Block {
    protected BarrierBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isFullBoundsCubeForCulling(BlockState blockState) {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return 1.0f;
    }

    @Override
    public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    }
}

