/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.BlockColumn;

public final class VerticalBlockSample
implements BlockColumn {
    private final int startY;
    private final BlockState[] states;

    public VerticalBlockSample(int startY, BlockState[] states) {
        this.startY = startY;
        this.states = states;
    }

    @Override
    public BlockState getState(int y) {
        int i = y - this.startY;
        if (i < 0 || i >= this.states.length) {
            return Blocks.AIR.getDefaultState();
        }
        return this.states[i];
    }

    @Override
    public void setState(int y, BlockState state) {
        int i = y - this.startY;
        if (i < 0 || i >= this.states.length) {
            throw new IllegalArgumentException("Outside of column height: " + y);
        }
        this.states[i] = state;
    }
}

