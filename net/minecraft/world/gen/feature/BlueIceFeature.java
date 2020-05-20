/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BlueIceFeature
extends Feature<DefaultFeatureConfig> {
    public BlueIceFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        if (blockPos.getY() > serverWorldAccess.getSeaLevel() - 1) {
            return false;
        }
        if (!serverWorldAccess.getBlockState(blockPos).isOf(Blocks.WATER) && !serverWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.WATER)) {
            return false;
        }
        boolean bl = false;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN || !serverWorldAccess.getBlockState(blockPos.offset(direction)).isOf(Blocks.PACKED_ICE)) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return false;
        }
        serverWorldAccess.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), 2);
        block1: for (int i = 0; i < 200; ++i) {
            BlockPos blockPos2;
            BlockState blockState;
            int j = random.nextInt(5) - random.nextInt(6);
            int k = 3;
            if (j < 2) {
                k += j / 2;
            }
            if (k < 1 || (blockState = serverWorldAccess.getBlockState(blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k)))).getMaterial() != Material.AIR && !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.PACKED_ICE) && !blockState.isOf(Blocks.ICE)) continue;
            for (Direction direction2 : Direction.values()) {
                BlockState blockState2 = serverWorldAccess.getBlockState(blockPos2.offset(direction2));
                if (!blockState2.isOf(Blocks.BLUE_ICE)) continue;
                serverWorldAccess.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), 2);
                continue block1;
            }
        }
        return true;
    }
}

