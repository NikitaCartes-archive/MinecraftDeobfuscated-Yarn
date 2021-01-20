/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public final class VerticalBlockSample {
    private final int startY;
    private final BlockState[] states;

    public VerticalBlockSample(int startY, BlockState[] states) {
        this.startY = startY;
        this.states = states;
    }

    public BlockState getState(BlockPos pos) {
        int i = pos.getY() - this.startY;
        if (i < 0 || i >= this.states.length) {
            return Blocks.AIR.getDefaultState();
        }
        return this.states[i];
    }
}

