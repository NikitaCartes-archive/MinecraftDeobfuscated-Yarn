/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import org.jetbrains.annotations.Nullable;

public class ChainedBlockSource
implements BlockSource {
    private final List<BlockSource> samplers;

    public ChainedBlockSource(List<BlockSource> samplers) {
        this.samplers = samplers;
    }

    @Override
    @Nullable
    public BlockState apply(ChunkNoiseSampler chunkNoiseSampler, int i, int j, int k) {
        for (BlockSource blockSource : this.samplers) {
            BlockState blockState = blockSource.apply(chunkNoiseSampler, i, j, k);
            if (blockState == null) continue;
            return blockState;
        }
        return null;
    }
}

