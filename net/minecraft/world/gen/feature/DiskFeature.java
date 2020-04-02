/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DiskFeature
extends Feature<DiskFeatureConfig> {
    public DiskFeature(Function<Dynamic<?>, ? extends DiskFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DiskFeatureConfig diskFeatureConfig) {
        if (!iWorld.getFluidState(blockPos).matches(FluidTags.WATER)) {
            return false;
        }
        int i = 0;
        int j = random.nextInt(diskFeatureConfig.radius - 2) + 2;
        for (int k = blockPos.getX() - j; k <= blockPos.getX() + j; ++k) {
            for (int l = blockPos.getZ() - j; l <= blockPos.getZ() + j; ++l) {
                int n;
                int m = k - blockPos.getX();
                if (m * m + (n = l - blockPos.getZ()) * n > j * j) continue;
                block2: for (int o = blockPos.getY() - diskFeatureConfig.ySize; o <= blockPos.getY() + diskFeatureConfig.ySize; ++o) {
                    BlockPos blockPos2 = new BlockPos(k, o, l);
                    BlockState blockState = iWorld.getBlockState(blockPos2);
                    for (BlockState blockState2 : diskFeatureConfig.targets) {
                        if (blockState2.getBlock() != blockState.getBlock()) continue;
                        iWorld.setBlockState(blockPos2, diskFeatureConfig.state, 2);
                        ++i;
                        continue block2;
                    }
                }
            }
        }
        return i > 0;
    }
}

