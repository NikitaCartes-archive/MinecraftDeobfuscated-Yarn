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
import java.util.List;
import java.util.OptionalInt;
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
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class AbstractTreeFeature
extends Feature<TreeFeatureConfig> {
    public AbstractTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> function) {
        super(function);
    }

    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Block block = state.getBlock();
            return state.isAir() || state.isIn(BlockTags.LEAVES) || AbstractTreeFeature.isDirt(block) || state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.SAPLINGS) || state.isOf(Blocks.VINE) || state.isOf(Blocks.WATER);
        });
    }

    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
    }

    private static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Block block = state.getBlock();
            return AbstractTreeFeature.isDirt(block) || block == Blocks.FARMLAND;
        });
    }

    private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Material material = state.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    public static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 19);
    }

    public static boolean canReplace(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
        return AbstractTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isWater(modifiableTestableWorld, blockPos);
    }

    private boolean generate(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config) {
        int p;
        BlockPos blockPos;
        int o;
        int i = config.trunkPlacer.getHeight(random);
        int j = config.foliagePlacer.getHeight(random, i, config);
        int k = i - j;
        int l = config.foliagePlacer.getRadius(random, k);
        if (!config.skipFluidCheck) {
            int m = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            int n = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            if (n - m > config.baseHeight) {
                return false;
            }
            o = config.heightmap == Heightmap.Type.OCEAN_FLOOR ? m : (config.heightmap == Heightmap.Type.WORLD_SURFACE ? n : world.getTopPosition(config.heightmap, pos).getY());
            blockPos = new BlockPos(pos.getX(), o, pos.getZ());
        } else {
            blockPos = pos;
        }
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        if (!AbstractTreeFeature.isDirtOrGrass(world, blockPos.down())) {
            return false;
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        OptionalInt optionalInt = config.featureSize.getMinClippedHeight();
        o = i;
        for (p = 0; p <= i + 1; ++p) {
            int q = config.featureSize.method_27378(i, p);
            block1: for (int r = -q; r <= q; ++r) {
                for (int s = -q; s <= q; ++s) {
                    mutable.set(blockPos, r, p, s);
                    if (AbstractTreeFeature.canTreeReplace(world, mutable) && (config.ignoreVines || !AbstractTreeFeature.isVine(world, mutable))) continue;
                    if (optionalInt.isPresent() && p - 1 >= optionalInt.getAsInt() + 1) {
                        o = p - 2;
                        continue block1;
                    }
                    return false;
                }
            }
        }
        p = o;
        List<FoliagePlacer.TreeNode> list = config.trunkPlacer.generate(world, random, p, blockPos, logPositions, box, config);
        list.forEach(treeNode -> treeFeatureConfig.foliagePlacer.generate(world, random, config, p, (FoliagePlacer.TreeNode)treeNode, j, l, leavesPositions));
        return true;
    }

    @Override
    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    @Override
    public final boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig) {
        HashSet<BlockPos> set = Sets.newHashSet();
        HashSet<BlockPos> set2 = Sets.newHashSet();
        HashSet<BlockPos> set3 = Sets.newHashSet();
        BlockBox blockBox = BlockBox.empty();
        boolean bl = this.generate(iWorld, random, blockPos, set, set2, blockBox, treeFeatureConfig);
        if (blockBox.minX > blockBox.maxX || !bl || set.isEmpty()) {
            return false;
        }
        if (!treeFeatureConfig.decorators.isEmpty()) {
            ArrayList<BlockPos> list = Lists.newArrayList(set);
            ArrayList<BlockPos> list2 = Lists.newArrayList(set2);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list2.sort(Comparator.comparingInt(Vec3i::getY));
            treeFeatureConfig.decorators.forEach(decorator -> decorator.generate(iWorld, random, list, list2, set3, blockBox));
        }
        VoxelSet voxelSet = this.placeLogsAndLeaves(iWorld, blockBox, set, set3);
        Structure.updateCorner(iWorld, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
        return true;
    }

    private VoxelSet placeLogsAndLeaves(IWorld world, BlockBox box, Set<BlockPos> logs, Set<BlockPos> leaves) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos : Lists.newArrayList(leaves)) {
            if (!box.contains(blockPos)) continue;
            ((VoxelSet)voxelSet).set(blockPos.getX() - box.minX, blockPos.getY() - box.minY, blockPos.getZ() - box.minZ, true, true);
        }
        for (BlockPos blockPos : Lists.newArrayList(logs)) {
            if (box.contains(blockPos)) {
                ((VoxelSet)voxelSet).set(blockPos.getX() - box.minX, blockPos.getY() - box.minY, blockPos.getZ() - box.minZ, true, true);
            }
            for (Direction direction : Direction.values()) {
                BlockState blockState;
                mutable.set(blockPos, direction);
                if (logs.contains(mutable) || !(blockState = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7)) continue;
                ((Set)list.get(0)).add(mutable.toImmutable());
                AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                if (!box.contains(mutable)) continue;
                ((VoxelSet)voxelSet).set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
            }
        }
        for (int k = 1; k < 6; ++k) {
            Set set = (Set)list.get(k - 1);
            Set set2 = (Set)list.get(k);
            for (BlockPos blockPos2 : set) {
                if (box.contains(blockPos2)) {
                    ((VoxelSet)voxelSet).set(blockPos2.getX() - box.minX, blockPos2.getY() - box.minY, blockPos2.getZ() - box.minZ, true, true);
                }
                for (Direction direction2 : Direction.values()) {
                    int l;
                    BlockState blockState2;
                    mutable.set(blockPos2, direction2);
                    if (set.contains(mutable) || set2.contains(mutable) || !(blockState2 = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                    BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                    AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
                    if (box.contains(mutable)) {
                        ((VoxelSet)voxelSet).set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
                    }
                    set2.add(mutable.toImmutable());
                }
            }
        }
        return voxelSet;
    }
}

