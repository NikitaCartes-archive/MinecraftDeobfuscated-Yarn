/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class AbstractTreeFeature<T extends FeatureConfig>
extends Feature<T> {
    public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> function, boolean bl) {
        super(function, bl);
    }

    protected static boolean canTreeReplace(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> {
            Block block = blockState.getBlock();
            return blockState.isAir() || blockState.matches(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || Block.isNaturalDirt(block) || block.matches(BlockTags.LOGS) || block.matches(BlockTags.SAPLINGS) || block == Blocks.VINE;
        });
    }

    protected static boolean isAir(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, BlockState::isAir);
    }

    protected static boolean isNaturalDirt(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
    }

    protected static boolean isWater(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> blockState.getBlock() == Blocks.WATER);
    }

    protected static boolean isLeaves(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> blockState.matches(BlockTags.LEAVES));
    }

    protected static boolean isAirOrLeaves(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
    }

    protected static boolean isNaturalDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> {
            Block block = blockState.getBlock();
            return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK;
        });
    }

    protected static boolean isDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> {
            Block block = blockState.getBlock();
            return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND;
        });
    }

    protected static boolean isReplaceablePlant(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> {
            Material material = blockState.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    protected void setToDirt(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
        if (!AbstractTreeFeature.isNaturalDirt(modifiableTestableWorld, blockPos)) {
            this.setBlockState(modifiableTestableWorld, blockPos, Blocks.DIRT.getDefaultState());
        }
    }

    @Override
    protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
    }

    protected final void setBlockState(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
        if (BlockTags.LOGS.contains(blockState.getBlock())) {
            set.add(blockPos.toImmutable());
        }
    }

    private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        if (this.emitNeighborBlockUpdates) {
            modifiableWorld.setBlockState(blockPos, blockState, 19);
        } else {
            modifiableWorld.setBlockState(blockPos, blockState, 18);
        }
    }

    @Override
    public final boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T featureConfig) {
        HashSet<BlockPos> set = Sets.newHashSet();
        boolean bl = this.generate(set, iWorld, random, blockPos);
        ArrayList list = Lists.newArrayList();
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            if (bl && !set.isEmpty()) {
                for (BlockPos blockPos2 : Lists.newArrayList(set)) {
                    for (Direction direction : Direction.values()) {
                        BlockState blockState;
                        pooledMutable.method_10114(blockPos2).method_10118(direction);
                        if (set.contains(pooledMutable) || !(blockState = iWorld.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7)) continue;
                        ((Set)list.get(0)).add(pooledMutable.toImmutable());
                        this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                    }
                }
            }
            for (int k = 1; k < 6; ++k) {
                Set set2 = (Set)list.get(k - 1);
                Set set3 = (Set)list.get(k);
                for (BlockPos blockPos3 : set2) {
                    for (Direction direction2 : Direction.values()) {
                        int l;
                        BlockState blockState2;
                        pooledMutable.method_10114(blockPos3).method_10118(direction2);
                        if (set2.contains(pooledMutable) || set3.contains(pooledMutable) || !(blockState2 = iWorld.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                        BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                        this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState3);
                        set3.add(pooledMutable.toImmutable());
                    }
                }
            }
        }
        return bl;
    }

    protected abstract boolean generate(Set<BlockPos> var1, ModifiableTestableWorld var2, Random var3, BlockPos var4);
}

