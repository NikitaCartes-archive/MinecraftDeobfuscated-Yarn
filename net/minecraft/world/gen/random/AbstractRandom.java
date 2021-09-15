/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.random;

import net.minecraft.world.gen.random.BlockPosRandomDeriver;

public interface AbstractRandom {
    public AbstractRandom derive();

    default public BlockPosRandomDeriver createBlockPosRandomDeriver() {
        return new BlockPosRandomDeriver(this.nextLong());
    }

    public void setSeed(long var1);

    public int nextInt();

    public int nextInt(int var1);

    public long nextLong();

    public boolean nextBoolean();

    public float nextFloat();

    public double nextDouble();

    public double nextGaussian();

    default public void skip(int count) {
        for (int i = 0; i < count; ++i) {
            this.nextInt();
        }
    }
}

