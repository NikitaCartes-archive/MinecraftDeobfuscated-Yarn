/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

public interface WorldGenRandom {
    public int nextInt();

    public int nextInt(int var1);

    public double nextDouble();

    default public void skip(int count) {
        for (int i = 0; i < count; ++i) {
            this.nextInt();
        }
    }
}

