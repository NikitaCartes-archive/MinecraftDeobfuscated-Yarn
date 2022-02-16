/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_6910;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public final class class_6955 {
    private static final float field_36620 = 0.4f;
    private static final int field_36621 = 20;
    private static final double field_36622 = 0.2;
    private static final float field_36623 = 0.7f;
    private static final float field_36624 = 0.1f;
    private static final float field_36625 = 0.3f;
    private static final float field_36626 = 0.6f;
    private static final float field_36627 = 0.02f;
    private static final float field_36628 = -0.3f;

    private class_6955() {
    }

    protected static ChunkNoiseSampler.BlockStateSampler method_40548(class_6910 arg, class_6910 arg2, class_6910 arg3, RandomDeriver randomDeriver) {
        BlockState blockState = null;
        return arg4 -> {
            double d = arg.method_40464(arg4);
            int i = arg4.blockY();
            VeinType veinType = d > 0.0 ? VeinType.COPPER : VeinType.IRON;
            double e = Math.abs(d);
            int j = veinType.maxY - i;
            int k = i - veinType.minY;
            if (k < 0 || j < 0) {
                return blockState;
            }
            int l = Math.min(j, k);
            double f = MathHelper.clampedLerpFromProgress((double)l, 0.0, 20.0, -0.2, 0.0);
            if (e + f < (double)0.4f) {
                return blockState;
            }
            AbstractRandom abstractRandom = randomDeriver.createRandom(arg4.blockX(), i, arg4.blockZ());
            if (abstractRandom.nextFloat() > 0.7f) {
                return blockState;
            }
            if (arg2.method_40464(arg4) >= 0.0) {
                return blockState;
            }
            double g = MathHelper.clampedLerpFromProgress(e, (double)0.4f, (double)0.6f, (double)0.1f, (double)0.3f);
            if ((double)abstractRandom.nextFloat() < g && arg3.method_40464(arg4) > (double)-0.3f) {
                return abstractRandom.nextFloat() < 0.02f ? veinType.rawBlock : veinType.ore;
            }
            return veinType.stone;
        };
    }

    protected static enum VeinType {
        COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
        IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

        final BlockState ore;
        final BlockState rawBlock;
        final BlockState stone;
        protected final int minY;
        protected final int maxY;

        private VeinType(BlockState ore, BlockState rawBlock, BlockState stone, int minY, int maxY) {
            this.ore = ore;
            this.rawBlock = rawBlock;
            this.stone = stone;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}

