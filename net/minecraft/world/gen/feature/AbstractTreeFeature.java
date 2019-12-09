/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class AbstractTreeFeature<T extends TreeFeatureConfig>
extends Feature<T> {
    public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> configFactory) {
        super(configFactory);
    }

    protected static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return blockState.isAir() || blockState.matches(BlockTags.LEAVES) || AbstractTreeFeature.isDirt(block) || block.matches(BlockTags.LOGS) || block.matches(BlockTags.SAPLINGS) || block == Blocks.VINE;
        });
    }

    public static boolean isAir(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, BlockState::isAir);
    }

    protected static boolean isNaturalDirt(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return AbstractTreeFeature.isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
        });
    }

    protected static boolean isLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.VINE);
    }

    public static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.WATER);
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
    }

    public static boolean isNaturalDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> AbstractTreeFeature.isDirt(blockState.getBlock()));
    }

    protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Block block = blockState.getBlock();
            return AbstractTreeFeature.isDirt(block) || block == Blocks.FARMLAND;
        });
    }

    public static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, blockState -> {
            Material material = blockState.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    protected void setToDirt(ModifiableTestableWorld world, BlockPos pos) {
        if (!AbstractTreeFeature.isNaturalDirt(world, pos)) {
            this.setBlockState(world, pos, Blocks.DIRT.getDefaultState());
        }
    }

    protected boolean setLogBlockState(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, BlockBox blockBox, TreeFeatureConfig config) {
        if (AbstractTreeFeature.isAirOrLeaves(world, pos) || AbstractTreeFeature.isReplaceablePlant(world, pos) || AbstractTreeFeature.isWater(world, pos)) {
            this.setBlockState(world, pos, config.trunkProvider.getBlockState(random, pos), blockBox);
            logPositions.add(pos.toImmutable());
            return true;
        }
        return false;
    }

    protected boolean setLeavesBlockState(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> leavesPositions, BlockBox blockBox, TreeFeatureConfig config) {
        if (AbstractTreeFeature.isAirOrLeaves(world, pos) || AbstractTreeFeature.isReplaceablePlant(world, pos) || AbstractTreeFeature.isWater(world, pos)) {
            this.setBlockState(world, pos, config.leavesProvider.getBlockState(random, pos), blockBox);
            leavesPositions.add(pos.toImmutable());
            return true;
        }
        return false;
    }

    @Override
    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    protected final void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, BlockBox blockBox) {
        this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
        blockBox.encompass(new BlockBox(blockPos, blockPos));
    }

    private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        modifiableWorld.setBlockState(blockPos, blockState, 19);
    }

    @Override
    public final boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T treeFeatureConfig) {
        HashSet<BlockPos> set = Sets.newHashSet();
        HashSet<BlockPos> set2 = Sets.newHashSet();
        HashSet<BlockPos> set3 = Sets.newHashSet();
        BlockBox blockBox = BlockBox.empty();
        boolean bl = this.generate(iWorld, random, blockPos, set, set2, blockBox, treeFeatureConfig);
        if (blockBox.minX > blockBox.maxX || !bl || set.isEmpty()) {
            return false;
        }
        if (!((TreeFeatureConfig)treeFeatureConfig).decorators.isEmpty()) {
            ArrayList<BlockPos> list = Lists.newArrayList(set);
            ArrayList<BlockPos> list2 = Lists.newArrayList(set2);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list2.sort(Comparator.comparingInt(Vec3i::getY));
            ((TreeFeatureConfig)treeFeatureConfig).decorators.forEach(treeDecorator -> treeDecorator.generate(iWorld, random, list, list2, set3, blockBox));
        }
        VoxelSet voxelSet = this.method_23380(iWorld, blockBox, set, set3);
        Structure.method_20532(iWorld, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
        return true;
    }

    private VoxelSet method_23380(IWorld iWorld, BlockBox blockBox, Set<BlockPos> set, Set<BlockPos> set2) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (BlockPos blockPos : Lists.newArrayList(set2)) {
                if (!blockBox.contains(blockPos)) continue;
                ((VoxelSet)voxelSet).set(blockPos.getX() - blockBox.minX, blockPos.getY() - blockBox.minY, blockPos.getZ() - blockBox.minZ, true, true);
            }
            for (BlockPos blockPos : Lists.newArrayList(set)) {
                if (blockBox.contains(blockPos)) {
                    ((VoxelSet)voxelSet).set(blockPos.getX() - blockBox.minX, blockPos.getY() - blockBox.minY, blockPos.getZ() - blockBox.minZ, true, true);
                }
                for (Direction direction : Direction.values()) {
                    BlockState blockState;
                    pooledMutable.set(blockPos).setOffset(direction);
                    if (set.contains(pooledMutable) || !(blockState = iWorld.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7)) continue;
                    ((Set)list.get(0)).add(pooledMutable.toImmutable());
                    this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                    if (!blockBox.contains(pooledMutable)) continue;
                    ((VoxelSet)voxelSet).set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
                }
            }
            for (int k = 1; k < 6; ++k) {
                Set set3 = (Set)list.get(k - 1);
                Set set4 = (Set)list.get(k);
                for (BlockPos blockPos2 : set3) {
                    if (blockBox.contains(blockPos2)) {
                        ((VoxelSet)voxelSet).set(blockPos2.getX() - blockBox.minX, blockPos2.getY() - blockBox.minY, blockPos2.getZ() - blockBox.minZ, true, true);
                    }
                    for (Direction direction2 : Direction.values()) {
                        int l;
                        BlockState blockState2;
                        pooledMutable.set(blockPos2).setOffset(direction2);
                        if (set3.contains(pooledMutable) || set4.contains(pooledMutable) || !(blockState2 = iWorld.getBlockState(pooledMutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                        BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                        this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState3);
                        if (blockBox.contains(pooledMutable)) {
                            ((VoxelSet)voxelSet).set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
                        }
                        set4.add(pooledMutable.toImmutable());
                    }
                }
            }
        }
        return voxelSet;
    }

    protected abstract boolean generate(ModifiableTestableWorld var1, Random var2, BlockPos var3, Set<BlockPos> var4, Set<BlockPos> var5, BlockBox var6, T var7);
}

