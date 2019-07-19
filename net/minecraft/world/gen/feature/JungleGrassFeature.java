/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class JungleGrassFeature
extends Feature<DefaultFeatureConfig> {
    public JungleGrassFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    public BlockState getGrass(Random random) {
        return random.nextInt(4) == 0 ? Blocks.FERN.getDefaultState() : Blocks.GRASS.getDefaultState();
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockState blockState = this.getGrass(random);
        BlockState blockState2 = iWorld.getBlockState(blockPos);
        while ((blockState2.isAir() || blockState2.matches(BlockTags.LEAVES)) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
            blockState2 = iWorld.getBlockState(blockPos);
        }
        int i = 0;
        for (int j = 0; j < 128; ++j) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || iWorld.getBlockState(blockPos2.down()).getBlock() == Blocks.PODZOL || !blockState.canPlaceAt(iWorld, blockPos2)) continue;
            iWorld.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }
}

