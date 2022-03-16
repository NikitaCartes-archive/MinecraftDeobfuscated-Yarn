/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_7124;
import net.minecraft.class_7128;
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
        BlockPos blockPos;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        if (!this.method_41571(structureWorldAccess, blockPos = context.getOrigin())) {
            return false;
        }
        SculkPatchFeatureConfig sculkPatchFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        class_7128 lv = class_7128.method_41485();
        int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();
        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
                lv.method_41482(blockPos, sculkPatchFeatureConfig.amountPerCharge());
            }
            boolean bl = j < sculkPatchFeatureConfig.spreadRounds();
            for (int l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
                lv.method_41479(structureWorldAccess, blockPos, random, bl);
            }
            lv.method_41494();
        }
        BlockPos blockPos2 = blockPos.down();
        if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance() && structureWorldAccess.getBlockState(blockPos2).isFullCube(structureWorldAccess, blockPos2)) {
            structureWorldAccess.setBlockState(blockPos, Blocks.SCULK_CATALYST.getDefaultState(), Block.NOTIFY_ALL);
        }
        return true;
    }

    private boolean method_41571(WorldAccess worldAccess, BlockPos blockPos2) {
        block5: {
            block4: {
                BlockState blockState = worldAccess.getBlockState(blockPos2);
                if (blockState.getBlock() instanceof class_7124) {
                    return true;
                }
                if (blockState.isAir()) break block4;
                if (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill()) break block5;
            }
            return Direction.stream().map(blockPos2::offset).anyMatch(blockPos -> worldAccess.getBlockState((BlockPos)blockPos).isFullCube(worldAccess, (BlockPos)blockPos));
        }
        return false;
    }
}

