/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public abstract class BlockStateProvider
implements DynamicSerializable {
    protected final BlockStateProviderType<?> stateProvider;

    protected BlockStateProvider(BlockStateProviderType<?> stateProvider) {
        this.stateProvider = stateProvider;
    }

    public abstract BlockState getBlockState(Random var1, BlockPos var2);
}

