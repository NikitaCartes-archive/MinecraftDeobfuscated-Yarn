/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class TreeFeature
extends Feature<TreeFeatureConfig> {
    private static final int FORCE_STATE_AND_NOTIFY_ALL = 19;

    public TreeFeature(Codec<TreeFeatureConfig> codec) {
        super(codec);
    }

    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
    }

    public static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Material material = state.getMaterial();
            return material == Material.REPLACEABLE_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT || material == Material.NETHER_SHOOTS;
        });
    }

    private static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
    }

    public static boolean canReplace(TestableWorld world, BlockPos pos) {
        return TreeFeature.isAirOrLeaves(world, pos) || TreeFeature.isReplaceablePlant(world, pos) || TreeFeature.isWater(world, pos);
    }

    private boolean generate(StructureWorldAccess world, Random random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, FoliagePlacer.BlockPlacer blockPlacer, TreeFeatureConfig config) {
        int i = config.trunkPlacer.getHeight(random);
        int j = config.foliagePlacer.getRandomHeight(random, i, config);
        int k = i - j;
        int l = config.foliagePlacer.getRandomRadius(random, k);
        BlockPos blockPos = config.rootPlacer.map(rootPlacer -> rootPlacer.trunkOffset(pos, random)).orElse(pos);
        int m = Math.min(pos.getY(), blockPos.getY());
        int n = Math.max(pos.getY(), blockPos.getY()) + i + 1;
        if (m < world.getBottomY() + 1 || n > world.getTopY()) {
            return false;
        }
        OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
        int o = this.getTopPosition(world, i, blockPos, config);
        if (o < i && (optionalInt.isEmpty() || o < optionalInt.getAsInt())) {
            return false;
        }
        if (config.rootPlacer.isPresent() && !config.rootPlacer.get().generate(world, rootPlacerReplacer, random, pos, blockPos, config)) {
            return false;
        }
        List<FoliagePlacer.TreeNode> list = config.trunkPlacer.generate(world, trunkPlacerReplacer, random, o, blockPos, config);
        list.forEach(node -> treeFeatureConfig.foliagePlacer.generate(world, blockPlacer, random, config, o, (FoliagePlacer.TreeNode)node, j, l));
        return true;
    }

    private int getTopPosition(TestableWorld world, int height, BlockPos pos, TreeFeatureConfig config) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i <= height + 1; ++i) {
            int j = config.minimumSize.getRadius(height, i);
            for (int k = -j; k <= j; ++k) {
                for (int l = -j; l <= j; ++l) {
                    mutable.set(pos, k, i, l);
                    if (config.trunkPlacer.canReplaceOrIsLog(world, mutable) && (config.ignoreVines || !TreeFeature.isVine(world, mutable))) continue;
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
    public final boolean generate(FeatureContext<TreeFeatureConfig> context) {
        final StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        TreeFeatureConfig treeFeatureConfig = context.getConfig();
        HashSet<BlockPos> set = Sets.newHashSet();
        HashSet<BlockPos> set2 = Sets.newHashSet();
        final HashSet<BlockPos> set3 = Sets.newHashSet();
        HashSet set4 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> biConsumer = (pos, state) -> {
            set.add(pos.toImmutable());
            structureWorldAccess.setBlockState((BlockPos)pos, (BlockState)state, Block.NOTIFY_ALL | Block.FORCE_STATE);
        };
        BiConsumer<BlockPos, BlockState> biConsumer2 = (pos, state) -> {
            set2.add(pos.toImmutable());
            structureWorldAccess.setBlockState((BlockPos)pos, (BlockState)state, Block.NOTIFY_ALL | Block.FORCE_STATE);
        };
        FoliagePlacer.BlockPlacer blockPlacer = new FoliagePlacer.BlockPlacer(){

            @Override
            public void placeBlock(BlockPos pos, BlockState state) {
                set3.add(pos.toImmutable());
                structureWorldAccess.setBlockState(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
            }

            @Override
            public boolean hasPlacedBlock(BlockPos pos) {
                return set3.contains(pos);
            }
        };
        BiConsumer<BlockPos, BlockState> biConsumer3 = (pos, state) -> {
            set4.add(pos.toImmutable());
            structureWorldAccess.setBlockState((BlockPos)pos, (BlockState)state, Block.NOTIFY_ALL | Block.FORCE_STATE);
        };
        boolean bl = this.generate(structureWorldAccess, random, blockPos, biConsumer, biConsumer2, blockPlacer, treeFeatureConfig);
        if (!bl || set2.isEmpty() && set3.isEmpty()) {
            return false;
        }
        if (!treeFeatureConfig.decorators.isEmpty()) {
            TreeDecorator.Generator generator = new TreeDecorator.Generator(structureWorldAccess, biConsumer3, random, set2, set3, set);
            treeFeatureConfig.decorators.forEach(decorator -> decorator.generate(generator));
        }
        return BlockBox.encompassPositions(Iterables.concat(set, set2, set3, set4)).map(box -> {
            VoxelSet voxelSet = TreeFeature.placeLogsAndLeaves(structureWorldAccess, box, set2, set4, set);
            StructureTemplate.updateCorner(structureWorldAccess, 3, voxelSet, box.getMinX(), box.getMinY(), box.getMinZ());
            return true;
        }).orElse(false);
    }

    private static VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> trunkPositions, Set<BlockPos> decorationPositions, Set<BlockPos> set) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos : Lists.newArrayList(Sets.union(decorationPositions, set))) {
            if (!box.contains(blockPos)) continue;
            ((VoxelSet)voxelSet).set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
        }
        for (BlockPos blockPos : Lists.newArrayList(trunkPositions)) {
            if (box.contains(blockPos)) {
                ((VoxelSet)voxelSet).set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
            }
            for (Direction direction : Direction.values()) {
                BlockState blockState;
                mutable.set((Vec3i)blockPos, direction);
                if (trunkPositions.contains(mutable) || !(blockState = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7)) continue;
                ((Set)list.get(0)).add(mutable.toImmutable());
                TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                if (!box.contains(mutable)) continue;
                ((VoxelSet)voxelSet).set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
            }
        }
        for (int k = 1; k < 6; ++k) {
            Set set2 = (Set)list.get(k - 1);
            Set set3 = (Set)list.get(k);
            for (BlockPos blockPos2 : set2) {
                if (box.contains(blockPos2)) {
                    ((VoxelSet)voxelSet).set(blockPos2.getX() - box.getMinX(), blockPos2.getY() - box.getMinY(), blockPos2.getZ() - box.getMinZ());
                }
                for (Direction direction2 : Direction.values()) {
                    int l;
                    BlockState blockState2;
                    mutable.set((Vec3i)blockPos2, direction2);
                    if (set2.contains(mutable) || set3.contains(mutable) || !(blockState2 = world.getBlockState(mutable)).contains(Properties.DISTANCE_1_7) || (l = blockState2.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                    BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                    TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
                    if (box.contains(mutable)) {
                        ((VoxelSet)voxelSet).set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
                    }
                    set3.add(mutable.toImmutable());
                }
            }
        }
        return voxelSet;
    }
}

