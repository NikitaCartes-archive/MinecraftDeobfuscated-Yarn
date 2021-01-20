/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
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
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class TreeFeature
extends Feature<TreeFeatureConfig> {
    public TreeFeature(Codec<TreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return TreeFeature.canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
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

    private static boolean canPlaceTreeOn(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> TreeFeature.isSoil(state) || state.isOf(Blocks.FARMLAND));
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

    public static boolean canReplace(TestableWorld world, BlockPos pos) {
        return TreeFeature.isAirOrLeaves(world, pos) || TreeFeature.isReplaceablePlant(world, pos) || TreeFeature.isWater(world, pos);
    }

    private boolean generate(StructureWorldAccess world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config) {
        BlockPos blockPos;
        int n;
        int i = config.trunkPlacer.getHeight(random);
        int j = config.foliagePlacer.getRandomHeight(random, i, config);
        int k = i - j;
        int l = config.foliagePlacer.getRandomRadius(random, k);
        if (!config.skipFluidCheck) {
            int m = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            n = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            if (n - m > config.maxWaterDepth) {
                return false;
            }
            int o = config.heightmap == Heightmap.Type.OCEAN_FLOOR ? m : (config.heightmap == Heightmap.Type.WORLD_SURFACE ? n : world.getTopPosition(config.heightmap, pos).getY());
            blockPos = new BlockPos(pos.getX(), o, pos.getZ());
        } else {
            blockPos = pos;
        }
        if (blockPos.getY() < world.getBottomSectionLimit() + 1 || blockPos.getY() + i + 1 > world.getTopHeightLimit()) {
            return false;
        }
        if (!TreeFeature.canPlaceTreeOn(world, blockPos.down())) {
            return false;
        }
        OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
        n = this.getTopPosition(world, i, blockPos, config);
        if (!(n >= i || optionalInt.isPresent() && n >= optionalInt.getAsInt())) {
            return false;
        }
        List<FoliagePlacer.TreeNode> list = config.trunkPlacer.generate(world, random, n, blockPos, logPositions, box, config);
        list.forEach(node -> treeFeatureConfig.foliagePlacer.generate(world, random, config, n, (FoliagePlacer.TreeNode)node, j, l, leavesPositions, box));
        return true;
    }

    private int getTopPosition(TestableWorld world, int height, BlockPos pos, TreeFeatureConfig config) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i <= height + 1; ++i) {
            int j = config.minimumSize.getRadius(height, i);
            for (int k = -j; k <= j; ++k) {
                for (int l = -j; l <= j; ++l) {
                    mutable.set(pos, k, i, l);
                    if (TreeFeature.canTreeReplace(world, mutable) && (config.ignoreVines || !TreeFeature.isVine(world, mutable))) continue;
                    return i - 2;
                }
            }
        }
        return height;
    }

    @Override
    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    @Override
    public final boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig) {
        HashSet<BlockPos> set = Sets.newHashSet();
        HashSet<BlockPos> set2 = Sets.newHashSet();
        HashSet<BlockPos> set3 = Sets.newHashSet();
        BlockBox blockBox = BlockBox.empty();
        boolean bl = this.generate(structureWorldAccess, random, blockPos, set, set2, blockBox, treeFeatureConfig);
        if (blockBox.minX > blockBox.maxX || !bl || set.isEmpty()) {
            return false;
        }
        if (!treeFeatureConfig.decorators.isEmpty()) {
            ArrayList<BlockPos> list = Lists.newArrayList(set);
            ArrayList<BlockPos> list2 = Lists.newArrayList(set2);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list2.sort(Comparator.comparingInt(Vec3i::getY));
            treeFeatureConfig.decorators.forEach(decorator -> decorator.generate(structureWorldAccess, random, list, list2, set3, blockBox));
        }
        VoxelSet voxelSet = this.placeLogsAndLeaves(structureWorldAccess, blockBox, set, set3);
        Structure.updateCorner(structureWorldAccess, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
        return true;
    }

    private VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> logs, Set<BlockPos> leaves) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos : Lists.newArrayList(leaves)) {
            if (!box.contains(blockPos)) continue;
            ((VoxelSet)voxelSet).set(blockPos.getX() - box.minX, blockPos.getY() - box.minY, blockPos.getZ() - box.minZ);
        }
        for (BlockPos blockPos : Lists.newArrayList(logs)) {
            if (box.contains(blockPos)) {
                ((VoxelSet)voxelSet).set(blockPos.getX() - box.minX, blockPos.getY() - box.minY, blockPos.getZ() - box.minZ);
            }
            for (Direction direction : Direction.values()) {
                BlockState blockState;
                mutable.set(blockPos, direction);
                if (logs.contains(mutable) || !(blockState = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7)) continue;
                ((Set)list.get(0)).add(mutable.toImmutable());
                TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                if (!box.contains(mutable)) continue;
                ((VoxelSet)voxelSet).set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ);
            }
        }
        for (int k = 1; k < 6; ++k) {
            Set set = (Set)list.get(k - 1);
            Set set2 = (Set)list.get(k);
            for (BlockPos blockPos2 : set) {
                if (box.contains(blockPos2)) {
                    ((VoxelSet)voxelSet).set(blockPos2.getX() - box.minX, blockPos2.getY() - box.minY, blockPos2.getZ() - box.minZ);
                }
                for (Direction direction2 : Direction.values()) {
                    int l;
                    BlockState blockState2;
                    mutable.set(blockPos2, direction2);
                    if (set.contains(mutable) || set2.contains(mutable) || !(blockState2 = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                    BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                    TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
                    if (box.contains(mutable)) {
                        ((VoxelSet)voxelSet).set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ);
                    }
                    set2.add(mutable.toImmutable());
                }
            }
        }
        return voxelSet;
    }
}

