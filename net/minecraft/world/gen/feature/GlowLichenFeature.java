/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GlowLichenFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GlowLichenFeature
extends Feature<GlowLichenFeatureConfig> {
    public GlowLichenFeature(Codec<GlowLichenFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<GlowLichenFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        GlowLichenFeatureConfig glowLichenFeatureConfig = context.getConfig();
        if (!GlowLichenFeature.isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        }
        List<Direction> list = GlowLichenFeature.shuffleDirections(glowLichenFeatureConfig, random);
        if (GlowLichenFeature.generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), glowLichenFeatureConfig, random, list)) {
            return true;
        }
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        block0: for (Direction direction : list) {
            mutable.set(blockPos);
            List<Direction> list2 = GlowLichenFeature.shuffleDirections(glowLichenFeatureConfig, random, direction.getOpposite());
            for (int i = 0; i < glowLichenFeatureConfig.searchRange; ++i) {
                mutable.set((Vec3i)blockPos, direction);
                BlockState blockState = structureWorldAccess.getBlockState(mutable);
                if (!GlowLichenFeature.isAirOrWater(blockState) && !blockState.isOf(glowLichenFeatureConfig.lichen)) continue block0;
                if (!GlowLichenFeature.generate(structureWorldAccess, mutable, blockState, glowLichenFeatureConfig, random, list2)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean generate(StructureWorldAccess world, BlockPos pos, BlockState state, GlowLichenFeatureConfig config, Random random, List<Direction> directions) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : directions) {
            BlockState blockState = world.getBlockState(mutable.set((Vec3i)pos, direction));
            if (!blockState.isIn(config.canPlaceOn)) continue;
            BlockState blockState2 = config.lichen.withDirection(state, world, pos, direction);
            if (blockState2 == null) {
                return false;
            }
            world.setBlockState(pos, blockState2, Block.NOTIFY_ALL);
            world.getChunk(pos).markBlockForPostProcessing(pos);
            if (random.nextFloat() < config.spreadChance) {
                config.lichen.getGrower().grow(blockState2, (WorldAccess)world, pos, direction, random, true);
            }
            return true;
        }
        return false;
    }

    public static List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random) {
        ArrayList<Direction> list = Lists.newArrayList(config.directions);
        Collections.shuffle(list, random);
        return list;
    }

    public static List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random, Direction excluded) {
        List<Direction> list = config.directions.stream().filter(direction -> direction != excluded).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list;
    }

    private static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }
}

