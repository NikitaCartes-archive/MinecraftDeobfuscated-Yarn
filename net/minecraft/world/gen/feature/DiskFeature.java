/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DiskFeature
extends Feature<DiskFeatureConfig> {
    public DiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskFeatureConfig diskFeatureConfig) {
        boolean bl = false;
        int i = diskFeatureConfig.radius.getValue(random);
        for (int j = blockPos.getX() - i; j <= blockPos.getX() + i; ++j) {
            for (int k = blockPos.getZ() - i; k <= blockPos.getZ() + i; ++k) {
                int m;
                int l = j - blockPos.getX();
                if (l * l + (m = k - blockPos.getZ()) * m > i * i) continue;
                block2: for (int n = blockPos.getY() - diskFeatureConfig.ySize; n <= blockPos.getY() + diskFeatureConfig.ySize; ++n) {
                    BlockPos blockPos2 = new BlockPos(j, n, k);
                    Block block = structureWorldAccess.getBlockState(blockPos2).getBlock();
                    for (BlockState blockState : diskFeatureConfig.targets) {
                        if (!blockState.isOf(block)) continue;
                        structureWorldAccess.setBlockState(blockPos2, diskFeatureConfig.state, 2);
                        bl = true;
                        continue block2;
                    }
                }
            }
        }
        return bl;
    }
}

