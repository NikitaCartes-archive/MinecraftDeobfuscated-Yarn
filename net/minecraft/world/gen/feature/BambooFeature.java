/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;

public class BambooFeature
extends Feature<ProbabilityConfig> {
    private static final BlockState BAMBOO = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.getDefaultState().with(BambooBlock.AGE, 1)).with(BambooBlock.LEAVES, BambooLeaves.NONE)).with(BambooBlock.STAGE, 0);
    private static final BlockState BAMBOO_TOP_1 = (BlockState)((BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE)).with(BambooBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_2 = (BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_3 = (BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.SMALL);

    public BambooFeature(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function);
    }

    public boolean method_12718(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, ProbabilityConfig probabilityConfig) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
        BlockPos.Mutable mutable2 = new BlockPos.Mutable(blockPos);
        if (iWorld.method_22347(mutable)) {
            if (Blocks.BAMBOO.getDefaultState().canPlaceAt(iWorld, mutable)) {
                int k;
                int j = random.nextInt(12) + 5;
                if (random.nextFloat() < probabilityConfig.probability) {
                    k = random.nextInt(4) + 1;
                    for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; ++l) {
                        for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k; ++m) {
                            int o;
                            int n = l - blockPos.getX();
                            if (n * n + (o = m - blockPos.getZ()) * o > k * k) continue;
                            mutable2.set(l, iWorld.getLightLevel(Heightmap.Type.WORLD_SURFACE, l, m) - 1, m);
                            if (!iWorld.getBlockState(mutable2).getBlock().matches(BlockTags.DIRT_LIKE)) continue;
                            iWorld.setBlockState(mutable2, Blocks.PODZOL.getDefaultState(), 2);
                        }
                    }
                }
                for (k = 0; k < j && iWorld.method_22347(mutable); ++k) {
                    iWorld.setBlockState(mutable, BAMBOO, 2);
                    mutable.setOffset(Direction.UP, 1);
                }
                if (mutable.getY() - blockPos.getY() >= 3) {
                    iWorld.setBlockState(mutable, BAMBOO_TOP_1, 2);
                    iWorld.setBlockState(mutable.setOffset(Direction.DOWN, 1), BAMBOO_TOP_2, 2);
                    iWorld.setBlockState(mutable.setOffset(Direction.DOWN, 1), BAMBOO_TOP_3, 2);
                }
            }
            ++i;
        }
        return i > 0;
    }
}

