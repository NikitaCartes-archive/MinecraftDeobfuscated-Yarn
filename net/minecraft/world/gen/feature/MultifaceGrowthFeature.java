/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MultifaceGrowthFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MultifaceGrowthFeature
extends Feature<MultifaceGrowthFeatureConfig> {
    public MultifaceGrowthFeature(Codec<MultifaceGrowthFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<MultifaceGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        AbstractRandom abstractRandom = context.getRandom();
        MultifaceGrowthFeatureConfig multifaceGrowthFeatureConfig = context.getConfig();
        if (!MultifaceGrowthFeature.isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        }
        List<Direction> list = multifaceGrowthFeatureConfig.shuffleDirections(abstractRandom);
        if (MultifaceGrowthFeature.generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), multifaceGrowthFeatureConfig, abstractRandom, list)) {
            return true;
        }
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        block0: for (Direction direction : list) {
            mutable.set(blockPos);
            List<Direction> list2 = multifaceGrowthFeatureConfig.shuffleDirections(abstractRandom, direction.getOpposite());
            for (int i = 0; i < multifaceGrowthFeatureConfig.searchRange; ++i) {
                mutable.set((Vec3i)blockPos, direction);
                BlockState blockState = structureWorldAccess.getBlockState(mutable);
                if (!MultifaceGrowthFeature.isAirOrWater(blockState) && !blockState.isOf(multifaceGrowthFeatureConfig.lichen)) continue block0;
                if (!MultifaceGrowthFeature.generate(structureWorldAccess, mutable, blockState, multifaceGrowthFeatureConfig, abstractRandom, list2)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean generate(StructureWorldAccess world, BlockPos pos, BlockState state, MultifaceGrowthFeatureConfig config, AbstractRandom random, List<Direction> directions) {
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

    private static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }
}

