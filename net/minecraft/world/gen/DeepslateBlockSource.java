/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;
import org.jetbrains.annotations.Nullable;

public class DeepslateBlockSource
implements BlockSource {
    private static final int DEFAULT_MIN_Y = -8;
    private static final int MAX_Y = 0;
    private final BlockPosRandomDeriver field_34587;
    private final BlockState deepslateState;

    public DeepslateBlockSource(BlockPosRandomDeriver blockPosRandomDeriver, BlockState deepslateState) {
        this.field_34587 = blockPosRandomDeriver;
        this.deepslateState = deepslateState;
    }

    @Override
    @Nullable
    public BlockState apply(ChunkNoiseSampler chunkNoiseSampler, int i, int j, int k) {
        if (j < -8) {
            return this.deepslateState;
        }
        if (j > 0) {
            return null;
        }
        double d = MathHelper.lerpFromProgress(j, -8.0f, 0.0f, 1.0f, 0.0f);
        AtomicSimpleRandom abstractRandom = this.field_34587.createRandom(i, j, k);
        return (double)abstractRandom.nextFloat() < d ? this.deepslateState : null;
    }
}

