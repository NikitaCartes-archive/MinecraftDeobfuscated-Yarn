/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;

public class IcePatchFeature
extends Feature<IcePatchFeatureConfig> {
    private final Block ICE = Blocks.PACKED_ICE;

    public IcePatchFeature(Codec<IcePatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, IcePatchFeatureConfig icePatchFeatureConfig) {
        while (serverWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.down();
        }
        if (!serverWorldAccess.getBlockState(blockPos).isOf(Blocks.SNOW_BLOCK)) {
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
                    Block block = serverWorldAccess.getBlockState(blockPos2).getBlock();
                    if (!IcePatchFeature.isDirt(block) && block != Blocks.SNOW_BLOCK && block != Blocks.ICE) continue;
                    serverWorldAccess.setBlockState(blockPos2, this.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

