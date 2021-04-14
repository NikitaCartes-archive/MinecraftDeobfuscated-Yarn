/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockSource {
    default public BlockState get(BlockPos pos) {
        return this.sample(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState sample(int var1, int var2, int var3);
}

