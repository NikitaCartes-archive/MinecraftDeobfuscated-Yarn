/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DeadBushFeature
extends Feature<DefaultFeatureConfig> {
    private static final DeadBushBlock DEAD_BUSH = (DeadBushBlock)Blocks.DEAD_BUSH;

    public DeadBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    public boolean method_12869(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockState blockState = iWorld.getBlockState(blockPos);
        while ((blockState.isAir() || blockState.matches(BlockTags.LEAVES)) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
            blockState = iWorld.getBlockState(blockPos);
        }
        BlockState blockState2 = DEAD_BUSH.getDefaultState();
        for (int i = 0; i < 4; ++i) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || !blockState2.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, blockState2, 2);
        }
        return true;
    }
}

