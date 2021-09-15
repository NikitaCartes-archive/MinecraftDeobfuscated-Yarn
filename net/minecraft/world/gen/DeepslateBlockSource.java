/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.class_6568;
import net.minecraft.class_6583;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;
import org.jetbrains.annotations.Nullable;

public class DeepslateBlockSource
implements class_6583 {
    private static final int DEFAULT_MIN_Y = -8;
    private static final int MAX_Y = 0;
    private final BlockPosRandomDeriver field_34587;
    private final BlockState deepslateState;

    public DeepslateBlockSource(BlockPosRandomDeriver blockPosRandomDeriver, BlockState blockState) {
        this.field_34587 = blockPosRandomDeriver;
        this.deepslateState = blockState;
    }

    @Override
    @Nullable
    public BlockState apply(class_6568 arg, int i, int j, int k) {
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

