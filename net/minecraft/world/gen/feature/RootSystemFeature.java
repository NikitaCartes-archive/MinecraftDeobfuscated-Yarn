/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RootSystemFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RootSystemFeature
extends Feature<RootSystemFeatureConfig> {
    public RootSystemFeature(Codec<RootSystemFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<RootSystemFeatureConfig> context) {
        BlockPos blockPos;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        if (!structureWorldAccess.getBlockState(blockPos = context.getOrigin()).isAir()) {
            return false;
        }
        Random random = context.getRandom();
        BlockPos blockPos2 = context.getOrigin();
        RootSystemFeatureConfig rootSystemFeatureConfig = context.getConfig();
        BlockPos.Mutable mutable = blockPos2.mutableCopy();
        if (RootSystemFeature.generateTreeAndRoots(structureWorldAccess, context.getGenerator(), rootSystemFeatureConfig, random, mutable, blockPos2)) {
            RootSystemFeature.generateHangingRoots(structureWorldAccess, rootSystemFeatureConfig, random, blockPos2, mutable);
        }
        return true;
    }

    private static boolean hasSpaceForTree(StructureWorldAccess world, RootSystemFeatureConfig config, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 1; i <= config.requiredVerticalSpaceForTree; ++i) {
            mutable.move(Direction.UP);
            BlockState blockState = world.getBlockState(mutable);
            if (RootSystemFeature.isAirOrWater(blockState, i, config.allowedVerticalWaterForTree)) continue;
            return false;
        }
        return true;
    }

    private static boolean isAirOrWater(BlockState state, int height, int allowedVerticalWaterForTree) {
        return state.isAir() || height <= allowedVerticalWaterForTree && state.getFluidState().isIn(FluidTags.WATER);
    }

    private static boolean generateTreeAndRoots(StructureWorldAccess world, ChunkGenerator generator, RootSystemFeatureConfig config, Random random, BlockPos.Mutable mutablePos, BlockPos pos) {
        for (int i = 0; i < config.maxRootColumnHeight; ++i) {
            mutablePos.move(Direction.UP);
            if (!config.predicate.test(world, mutablePos) || !RootSystemFeature.hasSpaceForTree(world, config, mutablePos)) continue;
            Vec3i blockPos = mutablePos.down();
            if (world.getFluidState((BlockPos)blockPos).isIn(FluidTags.LAVA) || !world.getBlockState((BlockPos)blockPos).getMaterial().isSolid()) {
                return false;
            }
            if (!config.feature.get().generateUnregistered(world, generator, random, mutablePos)) continue;
            RootSystemFeature.generateRootsColumn(pos, pos.getY() + i, world, config, random);
            return true;
        }
        return false;
    }

    private static void generateRootsColumn(BlockPos pos, int maxY, StructureWorldAccess world, RootSystemFeatureConfig config, Random random) {
        int i = pos.getX();
        int j = pos.getZ();
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int k = pos.getY(); k < maxY; ++k) {
            RootSystemFeature.generateRoots(world, config, random, i, j, mutable.set(i, k, j));
        }
    }

    private static void generateRoots(StructureWorldAccess world, RootSystemFeatureConfig config, Random random, int x, int z, BlockPos.Mutable mutablePos) {
        int i = config.rootRadius;
        Tag<Block> tag = BlockTags.getTagGroup().getTag(config.rootReplaceable);
        Predicate<BlockState> predicate = tag == null ? state -> true : state -> state.isIn(tag);
        for (int j = 0; j < config.rootPlacementAttempts; ++j) {
            mutablePos.set(mutablePos, random.nextInt(i) - random.nextInt(i), 0, random.nextInt(i) - random.nextInt(i));
            if (predicate.test(world.getBlockState(mutablePos))) {
                world.setBlockState(mutablePos, config.rootStateProvider.getBlockState(random, mutablePos), Block.NOTIFY_LISTENERS);
            }
            mutablePos.setX(x);
            mutablePos.setZ(z);
        }
    }

    private static void generateHangingRoots(StructureWorldAccess world, RootSystemFeatureConfig config, Random random, BlockPos pos, BlockPos.Mutable mutablePos) {
        int i = config.hangingRootRadius;
        int j = config.hangingRootVerticalSpan;
        for (int k = 0; k < config.hangingRootPlacementAttempts; ++k) {
            BlockState blockState;
            mutablePos.set(pos, random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            if (!world.isAir(mutablePos) || !(blockState = config.hangingRootStateProvider.getBlockState(random, mutablePos)).canPlaceAt(world, mutablePos) || !world.getBlockState((BlockPos)mutablePos.up()).isSideSolidFullSquare(world, mutablePos, Direction.DOWN)) continue;
            world.setBlockState(mutablePos, blockState, Block.NOTIFY_LISTENERS);
        }
    }
}

