/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.AbstractMountainSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.jetbrains.annotations.Nullable;

public class LoftyPeaksSurfaceBuilder
extends AbstractMountainSurfaceBuilder {
    private final AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig STEEP_SLOPE_BLOCK_CONFIG = new AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig(Blocks.STONE.getDefaultState(), true, false, false, true);

    public LoftyPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    @Nullable
    protected AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getLayerBlockConfig() {
        return this.STEEP_SLOPE_BLOCK_CONFIG;
    }

    @Override
    protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
        return config.getTopMaterial();
    }

    @Override
    protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
        return config.getUnderMaterial();
    }
}

