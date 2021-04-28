/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
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
    private static final int field_31520 = 16;
    private static final int field_31521 = 1;

    public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    private static int getDistance(int x1, int z1, int x2, int z2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkPos chunkPos = new ChunkPos(context.getOrigin());
        if (VoidStartPlatformFeature.getDistance(chunkPos.x, chunkPos.z, VoidStartPlatformFeature.START_CHUNK.x, VoidStartPlatformFeature.START_CHUNK.z) > 1) {
            return true;
        }
        BlockPos blockPos = START_BLOCK.withY(context.getOrigin().getY() + START_BLOCK.getY());
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); ++i) {
            for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); ++j) {
                if (VoidStartPlatformFeature.getDistance(blockPos.getX(), blockPos.getZ(), j, i) > 16) continue;
                mutable.set(j, blockPos.getY(), i);
                if (mutable.equals(blockPos)) {
                    structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                    continue;
                }
                structureWorldAccess.setBlockState(mutable, Blocks.STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
        }
        return true;
    }
}

