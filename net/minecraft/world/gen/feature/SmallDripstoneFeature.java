/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SmallDripstoneFeatureConfig;
import net.minecraft.world.gen.feature.util.DripstoneHelper;

public class SmallDripstoneFeature
extends Feature<SmallDripstoneFeatureConfig> {
    public SmallDripstoneFeature(Codec<SmallDripstoneFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<SmallDripstoneFeatureConfig> arg) {
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        BlockPos blockPos = arg.method_33655();
        Random random = arg.method_33654();
        SmallDripstoneFeatureConfig smallDripstoneFeatureConfig = arg.method_33656();
        if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
            return false;
        }
        int i = MathHelper.nextBetween(random, 1, smallDripstoneFeatureConfig.maxPlacements);
        boolean bl = false;
        for (int j = 0; j < i; ++j) {
            BlockPos blockPos2 = SmallDripstoneFeature.randomPos(random, blockPos, smallDripstoneFeatureConfig);
            if (!SmallDripstoneFeature.generate(structureWorldAccess, random, blockPos2, smallDripstoneFeatureConfig)) continue;
            bl = true;
        }
        return bl;
    }

    private static boolean generate(StructureWorldAccess world, Random random, BlockPos pos, SmallDripstoneFeatureConfig config) {
        Direction direction = Direction.random(random);
        Direction direction2 = random.nextBoolean() ? Direction.UP : Direction.DOWN;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < config.emptySpaceSearchRadius; ++i) {
            if (!DripstoneHelper.canGenerate(world, mutable)) {
                return false;
            }
            if (SmallDripstoneFeature.generateDripstone(world, random, mutable, direction2, config)) {
                return true;
            }
            if (SmallDripstoneFeature.generateDripstone(world, random, mutable, direction2.getOpposite(), config)) {
                return true;
            }
            mutable.move(direction);
        }
        return false;
    }

    private static boolean generateDripstone(StructureWorldAccess world, Random random, BlockPos pos, Direction direction, SmallDripstoneFeatureConfig config) {
        if (!DripstoneHelper.canGenerate(world, pos)) {
            return false;
        }
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        if (!DripstoneHelper.canReplace(blockState)) {
            return false;
        }
        SmallDripstoneFeature.generateDripstoneBlocks(world, random, blockPos);
        int i = random.nextFloat() < config.chanceOfTallerDripstone && DripstoneHelper.canGenerate(world, pos.offset(direction)) ? 2 : 1;
        DripstoneHelper.generatePointedDripstone(world, pos, direction, i, false);
        return true;
    }

    private static void generateDripstoneBlocks(StructureWorldAccess world, Random random, BlockPos pos) {
        DripstoneHelper.generateDripstoneBlock(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (random.nextFloat() < 0.3f) continue;
            BlockPos blockPos = pos.offset(direction);
            DripstoneHelper.generateDripstoneBlock(world, blockPos);
            if (random.nextBoolean()) continue;
            BlockPos blockPos2 = blockPos.offset(Direction.random(random));
            DripstoneHelper.generateDripstoneBlock(world, blockPos2);
            if (random.nextBoolean()) continue;
            BlockPos blockPos3 = blockPos2.offset(Direction.random(random));
            DripstoneHelper.generateDripstoneBlock(world, blockPos3);
        }
    }

    private static BlockPos randomPos(Random random, BlockPos pos, SmallDripstoneFeatureConfig config) {
        return pos.add(MathHelper.nextBetween(random, -config.maxOffsetFromOrigin, config.maxOffsetFromOrigin), MathHelper.nextBetween(random, -config.maxOffsetFromOrigin, config.maxOffsetFromOrigin), MathHelper.nextBetween(random, -config.maxOffsetFromOrigin, config.maxOffsetFromOrigin));
    }
}

