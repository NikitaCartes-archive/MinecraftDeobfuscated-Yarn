/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BushFeature
extends Feature<BushFeatureConfig> {
    public BushFeature(Function<Dynamic<?>, ? extends BushFeatureConfig> function) {
        super(function);
    }

    public boolean method_12841(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BushFeatureConfig bushFeatureConfig) {
        int i = 0;
        BlockState blockState = bushFeatureConfig.state;
        for (int j = 0; j < 64; ++j) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || iWorld.getDimension().isNether() && blockPos2.getY() >= 255 || !blockState.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }
}

