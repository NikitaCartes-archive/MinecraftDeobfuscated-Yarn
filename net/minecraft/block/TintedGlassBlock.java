/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class TintedGlassBlock
extends AbstractGlassBlock {
    public TintedGlassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return world.getMaxLightLevel();
    }
}

