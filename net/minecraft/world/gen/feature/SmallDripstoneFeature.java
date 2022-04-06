/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SmallDripstoneFeatureConfig;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SmallDripstoneFeature
extends Feature<SmallDripstoneFeatureConfig> {
    public SmallDripstoneFeature(Codec<SmallDripstoneFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SmallDripstoneFeatureConfig> context) {
        StructureWorldAccess worldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        AbstractRandom abstractRandom = context.getRandom();
        SmallDripstoneFeatureConfig smallDripstoneFeatureConfig = context.getConfig();
        Optional<Direction> optional = SmallDripstoneFeature.getDirection(worldAccess, blockPos, abstractRandom);
        if (optional.isEmpty()) {
            return false;
        }
        BlockPos blockPos2 = blockPos.offset(optional.get().getOpposite());
        SmallDripstoneFeature.generateDripstoneBlocks(worldAccess, abstractRandom, blockPos2, smallDripstoneFeatureConfig);
        int i = abstractRandom.nextFloat() < smallDripstoneFeatureConfig.chanceOfTallerDripstone && DripstoneHelper.canGenerate(worldAccess.getBlockState(blockPos.offset(optional.get()))) ? 2 : 1;
        DripstoneHelper.generatePointedDripstone(worldAccess, blockPos, optional.get(), i, false);
        return true;
    }

    private static Optional<Direction> getDirection(WorldAccess world, BlockPos pos, AbstractRandom random) {
        boolean bl = DripstoneHelper.canReplace(world.getBlockState(pos.up()));
        boolean bl2 = DripstoneHelper.canReplace(world.getBlockState(pos.down()));
        if (bl && bl2) {
            return Optional.of(random.nextBoolean() ? Direction.DOWN : Direction.UP);
        }
        if (bl) {
            return Optional.of(Direction.DOWN);
        }
        if (bl2) {
            return Optional.of(Direction.UP);
        }
        return Optional.empty();
    }

    private static void generateDripstoneBlocks(WorldAccess world, AbstractRandom random, BlockPos pos, SmallDripstoneFeatureConfig config) {
        DripstoneHelper.generateDripstoneBlock(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (random.nextFloat() > config.chanceOfDirectionalSpread) continue;
            BlockPos blockPos = pos.offset(direction);
            DripstoneHelper.generateDripstoneBlock(world, blockPos);
            if (random.nextFloat() > config.chanceOfSpreadRadius2) continue;
            BlockPos blockPos2 = blockPos.offset(Direction.random(random));
            DripstoneHelper.generateDripstoneBlock(world, blockPos2);
            if (random.nextFloat() > config.chanceOfSpreadRadius3) continue;
            BlockPos blockPos3 = blockPos2.offset(Direction.random(random));
            DripstoneHelper.generateDripstoneBlock(world, blockPos3);
        }
    }
}

