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

public class SnowcappedPeaksSurfaceBuilder
extends AbstractMountainSurfaceBuilder {
    private final AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig STEEP_SLOPE_BLOCK_CONFIG = new AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig(Blocks.PACKED_ICE.getDefaultState(), true, false, false, true);

    public SnowcappedPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    @Nullable
    protected AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getLayerBlockConfig() {
        return this.STEEP_SLOPE_BLOCK_CONFIG;
    }

    @Override
    protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
        BlockState blockState = this.getBlockFromNoise(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), 0.0, 0.025);
        blockState = this.getBlockFromNoise(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), 0.0, 0.2);
        return blockState;
    }

    @Override
    protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
        BlockState blockState = this.getBlockFromNoise(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), -0.0625, 0.025);
        blockState = this.getBlockFromNoise(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), -0.5, 0.2);
        return blockState;
    }
}

