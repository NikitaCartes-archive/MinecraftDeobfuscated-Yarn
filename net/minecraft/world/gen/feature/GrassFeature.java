/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GrassFeatureConfig;

public class GrassFeature
extends Feature<GrassFeatureConfig> {
    public GrassFeature(Function<Dynamic<?>, ? extends GrassFeatureConfig> function) {
        super(function);
    }

    public boolean method_14080(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, GrassFeatureConfig grassFeatureConfig) {
        BlockState blockState = iWorld.getBlockState(blockPos);
        while ((blockState.isAir() || blockState.matches(BlockTags.LEAVES)) && blockPos.getY() > 0) {
            blockPos = blockPos.method_10074();
            blockState = iWorld.getBlockState(blockPos);
        }
        int i = 0;
        for (int j = 0; j < 128; ++j) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || !grassFeatureConfig.state.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, grassFeatureConfig.state, 2);
            ++i;
        }
        return i > 0;
    }
}

