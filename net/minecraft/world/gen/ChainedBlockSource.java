/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseType;
import org.jetbrains.annotations.Nullable;

public record ChainedBlockSource(List<ChunkNoiseSampler.BlockStateSampler> samplers) implements ChunkNoiseSampler.BlockStateSampler
{
    @Override
    @Nullable
    public BlockState sample(NoiseType.NoisePos pos) {
        for (ChunkNoiseSampler.BlockStateSampler blockStateSampler : this.samplers) {
            BlockState blockState = blockStateSampler.sample(pos);
            if (blockState == null) continue;
            return blockState;
        }
        return null;
    }
}

