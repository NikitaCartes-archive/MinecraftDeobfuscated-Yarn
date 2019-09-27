/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;

public class IcePatchFeature
extends Feature<IcePatchFeatureConfig> {
    private final Block ICE = Blocks.PACKED_ICE;

    public IcePatchFeature(Function<Dynamic<?>, ? extends IcePatchFeatureConfig> function) {
        super(function);
    }

    public boolean method_13385(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, IcePatchFeatureConfig icePatchFeatureConfig) {
        while (iWorld.isAir(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.method_10074();
        }
        if (iWorld.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
            return false;
        }
        int i = random.nextInt(icePatchFeatureConfig.radius) + 2;
        boolean j = true;
        for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; ++k) {
            for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; ++l) {
                int n;
                int m = k - blockPos.getX();
                if (m * m + (n = l - blockPos.getZ()) * n > i * i) continue;
                for (int o = blockPos.getY() - 1; o <= blockPos.getY() + 1; ++o) {
                    BlockPos blockPos2 = new BlockPos(k, o, l);
                    Block block = iWorld.getBlockState(blockPos2).getBlock();
                    if (!Block.isNaturalDirt(block) && block != Blocks.SNOW_BLOCK && block != Blocks.ICE) continue;
                    iWorld.setBlockState(blockPos2, this.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

