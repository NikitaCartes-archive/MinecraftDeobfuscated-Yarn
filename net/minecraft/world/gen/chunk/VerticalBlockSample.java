/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_6557;

public final class VerticalBlockSample
implements class_6557 {
    private final int startY;
    private final BlockState[] states;

    public VerticalBlockSample(int startY, BlockState[] states) {
        this.startY = startY;
        this.states = states;
    }

    @Override
    public BlockState getState(int i) {
        int j = i - this.startY;
        if (j < 0 || j >= this.states.length) {
            return Blocks.AIR.getDefaultState();
        }
        return this.states[j];
    }

    @Override
    public void method_38092(int i, BlockState blockState) {
        int j = i - this.startY;
        if (j < 0 || j >= this.states.length) {
            throw new IllegalArgumentException("Outside of column height: " + i);
        }
        this.states[j] = blockState;
    }
}

