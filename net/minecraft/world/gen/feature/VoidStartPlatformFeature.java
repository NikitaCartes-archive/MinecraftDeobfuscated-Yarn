/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VoidStartPlatformFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
    private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);

    public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    private static int getDistance(int x1, int z1, int x2, int z2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkPos chunkPos = new ChunkPos(context.getPos());
        if (VoidStartPlatformFeature.getDistance(chunkPos.x, chunkPos.z, VoidStartPlatformFeature.START_CHUNK.x, VoidStartPlatformFeature.START_CHUNK.z) > 1) {
            return true;
        }
        BlockPos blockPos = context.getPos().add(START_BLOCK);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); ++i) {
            for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); ++j) {
                if (VoidStartPlatformFeature.getDistance(blockPos.getX(), blockPos.getZ(), j, i) > 16) continue;
                mutable.set(j, blockPos.getY(), i);
                if (mutable.equals(blockPos)) {
                    structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
                    continue;
                }
                structureWorldAccess.setBlockState(mutable, Blocks.STONE.getDefaultState(), 2);
            }
        }
        return true;
    }
}

