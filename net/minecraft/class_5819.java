/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

public interface class_5819 {
    public int nextInt();

    public int nextInt(int var1);

    public double nextDouble();

    default public void method_33650(int i) {
        for (int j = 0; j < i; ++j) {
            this.nextInt();
        }
    }
}

