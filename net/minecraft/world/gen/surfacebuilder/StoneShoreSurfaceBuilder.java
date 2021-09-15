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

public class StoneShoreSurfaceBuilder
extends AbstractMountainSurfaceBuilder {
    public StoneShoreSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    @Nullable
    protected AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getLayerBlockConfig() {
        return null;
    }

    @Override
    protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
        return this.getBlockFromNoise(0.03175, x, z, Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState(), -0.05, 0.05);
    }

    @Override
    protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
        return this.getBlockFromNoise(0.03175, x, z, Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState(), -0.05, 0.05);
    }
}

