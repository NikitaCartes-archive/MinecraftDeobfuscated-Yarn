/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BlueIceFeature
extends Feature<DefaultFeatureConfig> {
    public BlueIceFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        if (blockPos.getY() > iWorld.getSeaLevel() - 1) {
            return false;
        }
        if (iWorld.getBlockState(blockPos).getBlock() != Blocks.WATER && iWorld.getBlockState(blockPos.down()).getBlock() != Blocks.WATER) {
            return false;
        }
        boolean bl = false;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN || iWorld.getBlockState(blockPos.offset(direction)).getBlock() != Blocks.PACKED_ICE) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return false;
        }
        iWorld.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), 2);
        block1: for (int i = 0; i < 200; ++i) {
            int j = random.nextInt(5) - random.nextInt(6);
            int k = 3;
            if (j < 2) {
                k += j / 2;
            }
            if (k < 1) continue;
            BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
            BlockState blockState = iWorld.getBlockState(blockPos2);
            Block block = blockState.getBlock();
            if (blockState.getMaterial() != Material.AIR && block != Blocks.WATER && block != Blocks.PACKED_ICE && block != Blocks.ICE) continue;
            for (Direction direction2 : Direction.values()) {
                Block block2 = iWorld.getBlockState(blockPos2.offset(direction2)).getBlock();
                if (block2 != Blocks.BLUE_ICE) continue;
                iWorld.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), 2);
                continue block1;
            }
        }
        return true;
    }
}

