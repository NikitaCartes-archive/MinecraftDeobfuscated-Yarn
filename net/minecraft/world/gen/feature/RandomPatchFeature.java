/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class RandomPatchFeature
extends Feature<RandomPatchFeatureConfig> {
    public RandomPatchFeature(Function<Dynamic<?>, ? extends RandomPatchFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        BlockState blockState = randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
        BlockPos blockPos2 = randomPatchFeatureConfig.project ? serverWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, blockPos) : blockPos;
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = 0; j < randomPatchFeatureConfig.tries; ++j) {
            mutable.set(blockPos2, random.nextInt(randomPatchFeatureConfig.spreadX + 1) - random.nextInt(randomPatchFeatureConfig.spreadX + 1), random.nextInt(randomPatchFeatureConfig.spreadY + 1) - random.nextInt(randomPatchFeatureConfig.spreadY + 1), random.nextInt(randomPatchFeatureConfig.spreadZ + 1) - random.nextInt(randomPatchFeatureConfig.spreadZ + 1));
            Vec3i blockPos3 = mutable.down();
            BlockState blockState2 = serverWorldAccess.getBlockState((BlockPos)blockPos3);
            if (!serverWorldAccess.isAir(mutable) && (!randomPatchFeatureConfig.canReplace || !serverWorldAccess.getBlockState(mutable).getMaterial().isReplaceable()) || !blockState.canPlaceAt(serverWorldAccess, mutable) || !randomPatchFeatureConfig.whitelist.isEmpty() && !randomPatchFeatureConfig.whitelist.contains(blockState2.getBlock()) || randomPatchFeatureConfig.blacklist.contains(blockState2) || randomPatchFeatureConfig.needsWater && !serverWorldAccess.getFluidState(((BlockPos)blockPos3).west()).matches(FluidTags.WATER) && !serverWorldAccess.getFluidState(((BlockPos)blockPos3).east()).matches(FluidTags.WATER) && !serverWorldAccess.getFluidState(((BlockPos)blockPos3).north()).matches(FluidTags.WATER) && !serverWorldAccess.getFluidState(((BlockPos)blockPos3).south()).matches(FluidTags.WATER)) continue;
            randomPatchFeatureConfig.blockPlacer.method_23403(serverWorldAccess, mutable, blockState, random);
            ++i;
        }
        return i > 0;
    }
}

