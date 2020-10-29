/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.AbstractNetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class SoulSandValleySurfaceBuilder
extends AbstractNetherSurfaceBuilder {
    private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
    private static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final ImmutableList<BlockState> SURFACE_STATES = ImmutableList.of(SOUL_SAND, SOUL_SOIL);

    public SoulSandValleySurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<BlockState> getSurfaceStates() {
        return SURFACE_STATES;
    }

    @Override
    protected ImmutableList<BlockState> getUnderLavaStates() {
        return SURFACE_STATES;
    }

    @Override
    protected BlockState getLavaShoreState() {
        return GRAVEL;
    }
}

