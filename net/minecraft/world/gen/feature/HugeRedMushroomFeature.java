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

public class HugeRedMushroomFeature
extends Feature<PlantedFeatureConfig> {
    public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> function) {
        super(function);
    }

    public boolean method_13398(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, PlantedFeatureConfig plantedFeatureConfig) {
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
        Block block = iWorld.getBlockState(blockPos.down()).getBlock();
        if (!Block.isNaturalDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
            return false;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k <= i; ++k) {
            l = 0;
            if (k < i && k >= i - 3) {
                l = 2;
            } else if (k == i) {
                l = 1;
            }
            for (m = -l; m <= l; ++m) {
                for (n = -l; n <= l; ++n) {
                    BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
                    if (blockState.isAir() || blockState.matches(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        BlockState blockState2 = (BlockState)Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, false);
        for (l = i - 3; l <= i; ++l) {
            m = l < i ? 2 : 1;
            n = 0;
            for (int o = -m; o <= m; ++o) {
                for (int p = -m; p <= m; ++p) {
                    boolean bl6;
                    boolean bl = o == -m;
                    boolean bl2 = o == m;
                    boolean bl3 = p == -m;
                    boolean bl4 = p == m;
                    boolean bl5 = bl || bl2;
                    boolean bl7 = bl6 = bl3 || bl4;
                    if (l < i && bl5 == bl6) continue;
                    mutable.set(blockPos).setOffset(o, l, p);
                    if (iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) continue;
                    this.setBlockState(iWorld, mutable, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)blockState2.with(MushroomBlock.UP, l >= i - 1)).with(MushroomBlock.WEST, o < 0)).with(MushroomBlock.EAST, o > 0)).with(MushroomBlock.NORTH, p < 0)).with(MushroomBlock.SOUTH, p > 0));
                }
            }
        }
        BlockState blockState3 = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, false)).with(MushroomBlock.DOWN, false);
        for (m = 0; m < i; ++m) {
            mutable.set(blockPos).setOffset(Direction.UP, m);
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

