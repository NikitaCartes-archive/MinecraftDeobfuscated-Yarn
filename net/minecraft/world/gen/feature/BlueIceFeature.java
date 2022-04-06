/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlueIceFeature
extends Feature<DefaultFeatureConfig> {
    public BlueIceFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        AbstractRandom abstractRandom = context.getRandom();
        if (blockPos.getY() > structureWorldAccess.getSeaLevel() - 1) {
            return false;
        }
        if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.WATER) && !structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.WATER)) {
            return false;
        }
        boolean bl = false;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN || !structureWorldAccess.getBlockState(blockPos.offset(direction)).isOf(Blocks.PACKED_ICE)) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return false;
        }
        structureWorldAccess.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
        block1: for (int i = 0; i < 200; ++i) {
            BlockPos blockPos2;
            BlockState blockState;
            int j = abstractRandom.nextInt(5) - abstractRandom.nextInt(6);
            int k = 3;
            if (j < 2) {
                k += j / 2;
            }
            if (k < 1 || (blockState = structureWorldAccess.getBlockState(blockPos2 = blockPos.add(abstractRandom.nextInt(k) - abstractRandom.nextInt(k), j, abstractRandom.nextInt(k) - abstractRandom.nextInt(k)))).getMaterial() != Material.AIR && !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.PACKED_ICE) && !blockState.isOf(Blocks.ICE)) continue;
            for (Direction direction2 : Direction.values()) {
                BlockState blockState2 = structureWorldAccess.getBlockState(blockPos2.offset(direction2));
                if (!blockState2.isOf(Blocks.BLUE_ICE)) continue;
                structureWorldAccess.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
                continue block1;
            }
        }
        return true;
    }
}

