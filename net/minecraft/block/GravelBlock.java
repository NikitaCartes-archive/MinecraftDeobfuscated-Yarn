/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GravelBlock
extends FallingBlock {
    public GravelBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return -8356741;
    }
}

