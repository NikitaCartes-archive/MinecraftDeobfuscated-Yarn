/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.BlockSource;

public class DefaultBlockSource
implements BlockSource {
    private final BlockState state;

    public DefaultBlockSource(BlockState state) {
        this.state = state;
    }

    @Override
    public BlockState sample(int x, int y, int z) {
        return this.state;
    }
}

