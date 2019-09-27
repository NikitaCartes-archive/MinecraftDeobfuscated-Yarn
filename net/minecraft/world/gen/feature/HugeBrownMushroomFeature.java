/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlantedFeatureConfig;

public class HugeBrownMushroomFeature
extends Feature<PlantedFeatureConfig> {
    public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> function) {
        super(function);
    }

    public boolean method_13362(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, PlantedFeatureConfig plantedFeatureConfig) {
        int n;
        int m;
        int l;
        int j;
        int i = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        if ((j = blockPos.getY()) < 1 || j + i + 1 >= 256) {
            return false;
        }
        Block block = iWorld.getBlockState(blockPos.method_10074()).getBlock();
        if (!Block.isNaturalDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
            return false;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k <= 1 + i; ++k) {
            l = k <= 3 ? 0 : 3;
            for (m = -l; m <= l; ++m) {
                for (n = -l; n <= l; ++n) {
                    BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
                    if (blockState.isAir() || blockState.matches(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        BlockState blockState2 = (BlockState)((BlockState)Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.UP, true)).with(MushroomBlock.DOWN, false);
        l = 3;
        for (m = -3; m <= 3; ++m) {
            for (n = -3; n <= 3; ++n) {
                boolean bl6;
                boolean bl = m == -3;
                boolean bl2 = m == 3;
                boolean bl3 = n == -3;
                boolean bl4 = n == 3;
                boolean bl5 = bl || bl2;
                boolean bl7 = bl6 = bl3 || bl4;
                if (bl5 && bl6) continue;
                mutable.set(blockPos).setOffset(m, i, n);
                if (iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) continue;
                boolean bl72 = bl || bl6 && m == -2;
                boolean bl8 = bl2 || bl6 && m == 2;
                boolean bl9 = bl3 || bl5 && n == -2;
                boolean bl10 = bl4 || bl5 && n == 2;
                this.setBlockState(iWorld, mutable, (BlockState)((BlockState)((BlockState)((BlockState)blockState2.with(MushroomBlock.WEST, bl72)).with(MushroomBlock.EAST, bl8)).with(MushroomBlock.NORTH, bl9)).with(MushroomBlock.SOUTH, bl10));
            }
        }
        BlockState blockState3 = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, false)).with(MushroomBlock.DOWN, false);
        for (n = 0; n < i; ++n) {
            mutable.set(blockPos).setOffset(Direction.UP, n);
            if (iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) continue;
            if (plantedFeatureConfig.planted) {
                iWorld.setBlockState(mutable, blockState3, 3);
                continue;
            }
            this.setBlockState(iWorld, mutable, blockState3);
        }
        return true;
    }
}

