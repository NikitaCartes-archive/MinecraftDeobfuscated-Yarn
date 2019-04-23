/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class AbstractGlassBlock
extends TransparentBlock {
    protected AbstractGlassBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return 1.0f;
    }

    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    @Override
    public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    }
}

