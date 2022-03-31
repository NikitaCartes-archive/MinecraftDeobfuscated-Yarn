/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SculkPatchFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SculkPatchFeature
extends Feature<SculkPatchFeatureConfig> {
    public SculkPatchFeature(Codec<SculkPatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SculkPatchFeatureConfig> context) {
        int l;
        int k;
        BlockPos blockPos;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        if (!this.canGenerate(structureWorldAccess, blockPos = context.getOrigin())) {
            return false;
        }
        SculkPatchFeatureConfig sculkPatchFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        SculkSpreadManager sculkSpreadManager = SculkSpreadManager.createWorldGen();
        int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();
        for (int j = 0; j < i; ++j) {
            for (k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
                sculkSpreadManager.spread(blockPos, sculkPatchFeatureConfig.amountPerCharge());
            }
            boolean bl = j < sculkPatchFeatureConfig.spreadRounds();
            for (l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
                sculkSpreadManager.tick(structureWorldAccess, blockPos, random, bl);
            }
            sculkSpreadManager.clearCursors();
        }
        BlockPos blockPos2 = blockPos.down();
        if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance() && structureWorldAccess.getBlockState(blockPos2).isFullCube(structureWorldAccess, blockPos2)) {
            structureWorldAccess.setBlockState(blockPos, Blocks.SCULK_CATALYST.getDefaultState(), Block.NOTIFY_ALL);
        }
        k = sculkPatchFeatureConfig.extraRareGrowths().get(random);
        for (l = 0; l < k; ++l) {
            BlockPos blockPos3 = blockPos.add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            if (!structureWorldAccess.getBlockState(blockPos3).isAir() || !structureWorldAccess.getBlockState(blockPos3.down()).isSideSolidFullSquare(structureWorldAccess, blockPos3.down(), Direction.UP)) continue;
            structureWorldAccess.setBlockState(blockPos3, (BlockState)Blocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, true), Block.NOTIFY_ALL);
        }
        return true;
    }

    private boolean canGenerate(WorldAccess world, BlockPos pos) {
        block5: {
            block4: {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.getBlock() instanceof SculkSpreadable) {
                    return true;
                }
                if (blockState.isAir()) break block4;
                if (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill()) break block5;
            }
            return Direction.stream().map(pos::offset).anyMatch(pos2 -> world.getBlockState((BlockPos)pos2).isFullCube(world, (BlockPos)pos2));
        }
        return false;
    }
}

