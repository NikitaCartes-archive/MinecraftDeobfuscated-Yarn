/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class DeepslateBlockSource
implements BlockSource {
    private final RandomDeriver field_34587;
    @Nullable
    private final BlockState field_35137;
    @Nullable
    private final BlockState field_35138;
    private final int field_35139;
    private final int field_35140;

    public DeepslateBlockSource(RandomDeriver randomDeriver, @Nullable BlockState deepslateState, @Nullable BlockState blockState, int i, int j) {
        this.field_34587 = randomDeriver;
        this.field_35137 = deepslateState;
        this.field_35138 = blockState;
        this.field_35139 = i;
        this.field_35140 = j;
        Validate.isTrue(i < j, "Below bounds (" + i + ") need to be smaller than above bounds (" + j + ")", new Object[0]);
    }

    @Override
    @Nullable
    public BlockState apply(ChunkNoiseSampler chunkNoiseSampler, int i, int j, int k) {
        if (j <= this.field_35139) {
            return this.field_35137;
        }
        if (j >= this.field_35140) {
            return this.field_35138;
        }
        double d = MathHelper.lerpFromProgress((double)j, (double)this.field_35139, (double)this.field_35140, 1.0, 0.0);
        AbstractRandom abstractRandom = this.field_34587.createRandom(i, j, k);
        return (double)abstractRandom.nextFloat() < d ? this.field_35137 : this.field_35138;
    }
}

