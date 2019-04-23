/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public interface TestableWorld {
    public boolean testBlockState(BlockPos var1, Predicate<BlockState> var2);

    public BlockPos getTopPosition(Heightmap.Type var1, BlockPos var2);
}

