/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld {
    public boolean testBlockState(BlockPos var1, Predicate<BlockState> var2);
}

